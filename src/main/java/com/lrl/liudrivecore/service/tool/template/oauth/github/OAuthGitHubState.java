package com.lrl.liudrivecore.service.tool.template.oauth.github;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OAuthGitHubState {

    protected String state;

    protected Long timestamp;

    OAuthGitHubState(){

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDigest(){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(state.getBytes());
            md.update((""+timestamp).getBytes());

            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static OAuthGitHubState getFreshInstance(){
        char[] r = new char[10];
        for(int i = 0; i < 10; i++){

        }
        return null;
    }
}
