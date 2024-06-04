package com.lrl.liudrivecore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.service.UserAuthService;
import com.lrl.liudrivecore.service.util.template.frontendInteractive.UserWithLinkedAccount;
import com.lrl.liudrivecore.service.util.template.oauth.github.OAuthGitHubToken;
import com.lrl.liudrivecore.service.util.template.oauth.github.OAuthGitHubUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/v2")
public class OAuthController {

    private static Logger logger = LoggerFactory.getLogger(OAuthController.class);

    UserAuthService userAuthService;
    /**
     * GitHub oauth authorization reference: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps
     * Step 1: /oauth2/github endpoint accept user login request and redirect to github server
     * Step 2: /oauth2/github/redirect be called for the 1st time, with param "code" for security
     * Step 3: Use the "code" and state(optional) to request for Access Token, client_secret needed here
     * Step 4: /oauth2/github/redirect be called again, and handle the Access Token
     *
     */

    private final static String GITHUB_OAUTH_ENDPOINT = "https://github.com/login/oauth/authorize";
    private final static String GITHUB_OAUTH_ACCESS_TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";

    @Autowired
    OAuthController(UserAuthService userAuthService){
        this.userAuthService = userAuthService;
    }

    @RequestMapping(value = "/oauth2/github", method = RequestMethod.GET)
    public void githubOAuth(HttpServletResponse response, @Value("${drive.oauth.client-id}") String clientId) throws IOException {

        //StateRegistry.register(state...) 注册状态以避免第三方伪请求
        response.sendRedirect(buildGitHubOAuthUrl(
                clientId,
                List.of("user", "admin"), "http://localhost:8080/v2/oauth2/github/redirect"));


    }


    // Code based on chatGPT solution and GitHub doc
    @RequestMapping(value = "/oauth2/github/redirect", method = RequestMethod.GET)
    public void setGithubOauthRedirectEndpoint(HttpServletRequest request, HttpServletResponse response,
                                               @Value("${drive.oauth.client-id}") String clientId,
                                               @Value("${drive.oauth.client-secret}") String clientSecret) throws IOException {

        System.out.println("Handling github oauth redirect");
        //Check state registry 防止第三方伪请求
        String code = request.getParameter("code");
        System.out.println("1 "+code);


        if(code != null){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Accept", "application/json");


            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);
            body.add("redirect_uri", "http://localhost:8080/v2/oauth2/github/redirect");

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

//
            ResponseEntity<String> responseEntity = restTemplate.exchange(GITHUB_OAUTH_ACCESS_TOKEN_ENDPOINT, HttpMethod.POST, requestEntity, String.class);

            //这里body就是一个json字符串，由于前面设置了Accept Header，Github doc里面提到了
            // Some parameters are "XX_XX" snake formatted. Using handmade deserializer to decode.
            OAuthGitHubToken oAuthGitHubToken = OAuthGitHubToken.buildFromString(responseEntity.getBody());
            System.out.println("OAuthGithubToken: "+ oAuthGitHubToken);
            System.out.println("OAuthGithubToken status: "+ responseEntity.getStatusCode());

            // 接收到200 响应时，查看是否有用户绑定
            // 有用户映射记录，以API M4.1.1流程返回User
            OAuthGitHubUserInfo userInfo = getUserInfo(oAuthGitHubToken);

            ObjectMapper mapper = new ObjectMapper();

            if(userInfo != null){
                User user = userAuthService.getUserMapping(userInfo);
                if (user == null) {
                    UserWithLinkedAccount user1 = userAuthService.prepareUserTemplate(userInfo, "github");
                    response.getWriter().write(mapper.writeValueAsString(user1));
                    response.setStatus(401);

                    response.sendRedirect("/?request=login&method=github&status=401&userId="+user1.getUserId()
                            +"&url="+user1.getUrl());
                }else {

                    response.getWriter().write(mapper.writeValueAsString(user));
                    response.setStatus(200);
                    response.sendRedirect("/?request=login&method=github&status=200&userId="+user.getUserId());
                }
                response.setContentType("Application/json");

                // Now we can access some user info from github

            }else {
                response.sendRedirect("/?request=login&method=github&code=400");
            }



        }else {
            response.sendRedirect("/?request=login&code=400");
        }



    }

    private OAuthGitHubUserInfo getUserInfo(OAuthGitHubToken oAuthGitHubToken) {

        String url = "https://api.github.com/user";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+oAuthGitHubToken.getAccessToken());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            logger.info("Get OAuthGitHubUserInfo: "+ oAuthGitHubToken);
            return OAuthGitHubUserInfo.buildFromBody(responseEntity.getBody());
        }
        else {
            logger.error("Get GitHub user info response with null or bad value: " + responseEntity.getStatusCode());
            return null;
        }
    }

    // Code based on GitHub doc
    @RequestMapping(value = "/oauth2/github/tokenEndpoint", method = RequestMethod.GET)
    public void setGithubOauthFinalEndpoint(HttpServletRequest request, HttpServletResponse response,
                                               @Value("${drive.oauth.client-id}") String clientId,
                                               @Value("${drive.oauth.client-secret}") String clientSecret) throws IOException {

        System.out.println("Handling github oauth final endpoint");
        //Check state registry 防止第三方伪请求


    }


    private String buildGitHubOAuthUrl(String clientId, List<String> scopes, String redirectUri) {

        StringBuilder sb = new StringBuilder(GITHUB_OAUTH_ENDPOINT)
                .append("?")
                .append("client_id=").append(clientId).append("&")
                .append("scopes=").append(stringifyScopes(scopes)).append("&")
                .append("redirect_uri=").append(redirectUri);

        return sb.toString();
    }

    private String stringifyScopes(List<String> scopes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : scopes) {
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
