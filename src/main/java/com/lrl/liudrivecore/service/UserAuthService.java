package com.lrl.liudrivecore.service;


import com.lrl.liudrivecore.data.pojo.OAuthMapping;
import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.data.repo.OAuthMappingRepository;
import com.lrl.liudrivecore.data.repo.UserRepository;
import com.lrl.liudrivecore.service.util.mune.TokenSpan;
import com.lrl.liudrivecore.service.security.TokenManager;
import com.lrl.liudrivecore.service.util.template.UserAuthTemplate;
import com.lrl.liudrivecore.service.util.template.frontendInteractive.UserWithLinkedAccount;
import com.lrl.liudrivecore.service.util.template.oauth.github.OAuthGitHubUserInfo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;


@Service
public class UserAuthService {

    private final TokenManager tokenManager;

    private UserRepository userRepository;

    private OAuthMappingRepository oAuthMappingRepository;

    @Autowired
    public UserAuthService(UserRepository repository,
                           OAuthMappingRepository oAuthMappingRepository,
                           TokenManager tokenManager) {
        this.userRepository = repository;
        this.tokenManager = tokenManager;
        this.oAuthMappingRepository = oAuthMappingRepository;
    }

    private static TokenSpan defaultSpan = TokenSpan.INITIAL_SPAN;

    private static Logger logger = LoggerFactory.getLogger(UserAuthService.class);

    /**
     * User authentication using USERNAME and PASSWORD.
     *
     * @param template Data template which holds user uploaded info
     * @return true - authentication passed.
     * false - authentication failed or other exception during authentication.
     */
    public User authenticate(UserAuthTemplate template) {

        logger.info("Authentication:" + template.toString());

        if (template.getPassword() == null || template.getPassword().equals("")) {
            logger.info("Authentication failed: No password or token found.");
            return null;
        } else {
            return verifyPasswordAndGetUser(template.getUsername(), template.getPassword());
        }
    }


    /**
     * Hand over the userId to TokenManager
     *
     * @param userId
     * @return token
     */
    public String registerToken(String userId) {
        return null;
    }


    /**
     * Register user into database
     *
     * @param user
     * @return
     */
    public User register(User user) {

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setAccountCreated(ZonedDateTime.now());

        if (userRepository.findByUsername(user.getUsername()) == null) {
            return userRepository.save(user);
        } else return null;

    }

    /**
     * @param newUserInfo
     */
    public User updateUserInfo(User newUserInfo) {

        User user = userRepository.findByUsername(newUserInfo.getUsername());


        user.setPassword(newUserInfo.getPassword());


        return userRepository.save(user);
    }


    private User verifyPasswordAndGetUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;

        boolean checkResult = BCrypt.checkpw(password, user.getPassword());
        if (!checkResult) {
            logger.info("Authentication failed: Password");
            return null;
        } else return user;

    }


    public boolean hasUser(String userId) {

        return userRepository.findByUserId(userId) != null;
    }


    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId);

        userRepository.delete(user);
        tokenManager.removeOwnerRecord(user.getUserId());
    }

    /**
     * This function is related to OAuth2 endpoints for user login and registration
     * Look up in the mapping table to see if user has already registered
     *
     * @param userInfo
     * @return
     */
    public User getUserMapping(OAuthGitHubUserInfo userInfo) {
        OAuthMapping om = oAuthMappingRepository.getOAuthMappingByUrl(userInfo.getUrl());
        if (om != null) {
            return userRepository.findByUserId(om.getUserId());
        } else return null;

    }


    /**
     * This function is related to OAuth2 endpoints for user login and registration
     *
     * @param userInfo
     * @return
     */
    public UserWithLinkedAccount prepareUserTemplate(OAuthGitHubUserInfo userInfo, String method) {

        UserWithLinkedAccount user = new UserWithLinkedAccount();
        user.setUserId("user-" + userInfo.getDigest().substring(0, 10));
        user.setUsername(userInfo.getEmail() == null ? userInfo.getName() : userInfo.getEmail());
        user.addMethod(userInfo.getUrl());
        user.addMethod(method);
        user.addUrl(userInfo.getUrl());
        return user;
    }

    public String attachToken(User user) {

        return tokenManager.generateToken(user, TokenSpan.INITIAL_SPAN);

    }

    public String updateToken(String userId, String token) {

        return tokenManager.updateToken(userId, TokenSpan.SHORT_SPAN, token);

    }

    public boolean validToken(String userId, String authorizationHeaderValue) {

        if (authorizationHeaderValue == null || authorizationHeaderValue.equals("")) return false;

        String[] parts = authorizationHeaderValue.split(" ");
        if (parts[0].equals("Bearer")) {
            return tokenManager.validToken(userId, parts[1]);
        } else if (parts[0].equals("Basic")) {

            String[] pair = new String(Base64.getDecoder().decode(parts[1])).split(":");
            User u = verifyPasswordAndGetUser(pair[0], pair[1]);
            if (u == null || u.getUserId() != userId) return false;
            else return true;
        }

        return false;
    }

    public UserAuthTemplate getUser(String userId) {
        if(hasUser(userId)){
            return UserAuthTemplate.safeCopy(userRepository.findByUserId(userId));
        }else return null;
    }
}
