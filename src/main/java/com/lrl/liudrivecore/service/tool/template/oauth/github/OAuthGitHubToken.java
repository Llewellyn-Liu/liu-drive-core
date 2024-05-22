package com.lrl.liudrivecore.service.tool.template.oauth.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthGitHubToken {

    private String accessToken;

    private String scope;

    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return "OAuthGitHubToken{" +
                "accessToken='" + accessToken + '\'' +
                ", scope='" + scope + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }

    public static OAuthGitHubToken buildFromString(String jsonFormatBody){
        if(jsonFormatBody == null) return null;
        OAuthGitHubToken oAuthGitHubToken = new OAuthGitHubToken();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonFormatBody);
            oAuthGitHubToken.setAccessToken(root.get("access_token").asText());
            oAuthGitHubToken.setTokenType(root.get("token_type").asText());
            oAuthGitHubToken.setScope(root.get("scope").asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return oAuthGitHubToken;

    }
}
