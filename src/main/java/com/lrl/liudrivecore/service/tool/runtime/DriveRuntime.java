package com.lrl.liudrivecore.service.tool.runtime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriveRuntime {

    private SessionManager sessionManager;

    private TokenManager tokenManager;

    @Autowired
    public DriveRuntime(SessionManager sessionManager, TokenManager tokenManager){
        this.sessionManager = sessionManager;
        this.tokenManager = tokenManager;
    }

    /**
     * @param userId
     * @param token
     * @param sessionId
     * @return
     */
    public boolean validate(String userId, String token, String sessionId) {

        if (tokenManager.validToken(token)
                && sessionManager.contains(userId, sessionId)) return true;

        return tokenManager.validToken(token)
                && sessionManager.contains(userId, sessionId);
    }
}
