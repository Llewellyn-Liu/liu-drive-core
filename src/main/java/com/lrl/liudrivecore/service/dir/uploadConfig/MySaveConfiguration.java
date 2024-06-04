package com.lrl.liudrivecore.service.dir.uploadConfig;

public class MySaveConfiguration {


    String method;

    Integer compressed;

    String token;

    public MySaveConfiguration(){

    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getCompressed() {
        return compressed;
    }

    public void setCompressed(Integer compressed) {
        this.compressed = compressed;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "DefaultSaveConfiguration{" +
                "method='" + method + '\'' +
                ", compressed=" + compressed +
                ", token='" + token + '\'' +
                '}';
    }
}