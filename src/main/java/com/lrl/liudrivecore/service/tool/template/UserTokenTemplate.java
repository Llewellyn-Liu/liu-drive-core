package com.lrl.liudrivecore.service.tool.template;

public class UserTokenTemplate {

    private String token;

    private String userId;

    public UserTokenTemplate() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserTokenTemplate{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
