package com.lrl.liudrivecore.service.util.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class UserWithLinkedAccount extends User {

    private List<String> method;
    private List<String> url;

    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public void addMethod(String v){
        if(method == null) method = new ArrayList<>();
        method.add(v);
    }

    public void addUrl(String u) {
        if(this.url == null) url = new ArrayList<>();
        url.add(u);
    }

    @Override
    public String toString() {
        return "UserWithLinkedAccount{" +
                "url=" + url +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accountCreated=" + accountCreated +
                ", userId='" + userId + '\'' +
                '}';
    }


}
