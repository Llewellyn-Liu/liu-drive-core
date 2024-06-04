package com.lrl.liudrivecore.service.util.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.service.util.template.UserAuthTemplate;

public class UserInfoAlterationTemplate {

    private String userId;

    private String username;

    private String password;

    private String token;

    private String newPassword;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserAuthTemplate getUserAuthTemplate(){
        UserAuthTemplate template = new UserAuthTemplate();
        template.setUsername(username);
        template.setPassword(password);
        template.setToken(token);
        template.setUserId(userId);

        return template;
    }

    public User getNewUserInfo(){
        return new User(username, password, userId);
    }
}
