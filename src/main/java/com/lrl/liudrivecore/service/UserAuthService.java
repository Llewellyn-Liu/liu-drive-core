package com.lrl.liudrivecore.service;


import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.data.repo.UserRepository;
import com.lrl.liudrivecore.service.tool.mune.TokenSpan;
import com.lrl.liudrivecore.service.tool.runtime.SessionManager;
import com.lrl.liudrivecore.service.tool.runtime.TokenManager;
import com.lrl.liudrivecore.service.tool.template.TokenTemplate;
import com.lrl.liudrivecore.service.tool.template.UserAuthTemplate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
public class UserAuthService {

    private final TokenManager tokenManager;
    private final SessionManager sessionManager;
    private UserRepository userRepository;

    @Autowired
    public UserAuthService(UserRepository repository,
                           TokenManager tokenManager, SessionManager sessionManager) {
        this.userRepository = repository;
        this.tokenManager = tokenManager;
        this.sessionManager = sessionManager;
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

        if (template.getPassword() == null && template.getToken() == null) {
            logger.info("Authentication failed: No password or token found.");
            return null;
        } else {
            return verifyPasswordAndGetUser(template.getUsername(), template.getPassword());
        }
    }

    /**
     * Quick authentication which doesn't need persistence layer queries. Credentials are saved in runtime memory.
     *
     * @param userAuthData
     * @return true - authentication passed.
     * false - authentication failed or other exception during authentication.
     */
    public boolean quickAuth(UserAuthTemplate userAuthData, String sessionId) {

        boolean validSession = sessionManager.contains(userAuthData.getUserId(), sessionId);

        boolean validToken = tokenManager.validToken(userAuthData.getToken());

        //Questionable: Should every request validate the timestamp or trust the TokenManager can clean the expired token immediately
//        boolean validTimestamp = TokenManager.getInstance().validTimestamp();

        return validSession && validToken;

    }

    /**
     * Hand over the userId to TokenManager
     *
     * @param userId
     * @return token
     */
    public String registerToken(String userId) {

        TokenTemplate tokenTemplate = tokenManager.generateTokenElement(userId, defaultSpan);
        tokenManager.push(tokenTemplate);
        return tokenTemplate.getToken();
    }

    /**
     * Extend token state for userId
     *
     * @param userAuthData
     * @param tokenSpan
     * @return
     */
    public TokenTemplate updateToken(UserAuthTemplate userAuthData, TokenSpan tokenSpan) {

        TokenTemplate newToken = tokenManager.generateTokenElement(userAuthData.getUserId(), tokenSpan);
        tokenManager.update(newToken);

        return newToken;
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
        String oldUserId = user.getUserId();

        user.setPassword(newUserInfo.getPassword());
        user.setUserId(newUserInfo.getUserId());

        return userRepository.save(user);
    }


    private User verifyPasswordAndGetUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null) return null;

        boolean checkResult = BCrypt.checkpw(password, user.getPassword());
        if (!checkResult) {
            logger.info("Authentication failed: Password");
            return null;
        } else return user;

    }

    public void registerSession(String userId, String id) {
        sessionManager.registerSession(userId, id);
    }

    public boolean hasUser(String username) {

        return userRepository.findByUsername(username) != null;
    }


    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username);

        int result = userRepository.deleteUserByUsername(username);
        System.out.println(result);
        tokenManager.removeOwnerRecord(user.getUserId());
    }
}
