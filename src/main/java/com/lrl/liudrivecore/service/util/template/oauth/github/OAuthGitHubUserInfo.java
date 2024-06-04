package com.lrl.liudrivecore.service.util.template.oauth.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OAuthGitHubUserInfo {

    String login;

    String name;

    String email;

    String url;

    String method;

    public OAuthGitHubUserInfo(){

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public static OAuthGitHubUserInfo buildFromBody(String body){
        if(body == null) return null;

        OAuthGitHubUserInfo oi = new OAuthGitHubUserInfo();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            oi.setLogin(root.get("login").asText());
            oi.setName(root.get("name").asText());
            oi.setEmail(root.get("email").asText());
            oi.setUrl(root.get("html_url").asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return oi;
    }

    public String getDigest(){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] encoded=  md.digest(toString().getBytes());
            return Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "OAuthGitHubUserInfo{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
