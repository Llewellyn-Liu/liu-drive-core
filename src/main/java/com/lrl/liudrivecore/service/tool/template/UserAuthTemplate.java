package com.lrl.liudrivecore.service.tool.template;

import com.lrl.liudrivecore.data.pojo.User;

public class UserAuthTemplate {

    private String username;

    private String userId;

    private String password;

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static UserAuthTemplate safeCopyAndAddToken(User user, String token){
        UserAuthTemplate newTemplate = new UserAuthTemplate();
        newTemplate.setUsername(user.getUsername());
        newTemplate.setUserId(user.getUserId());
        newTemplate.setToken(token);

        return newTemplate;
    }

    public static UserAuthTemplate safeCopy(User user){
        UserAuthTemplate newTemplate = new UserAuthTemplate();
        newTemplate.setUsername(user.getUsername());
        newTemplate.setUserId(user.getUserId());

        return newTemplate;
    }

    @Override
    public String toString() {
        return "UserAuthTemplate{" +
                "username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
