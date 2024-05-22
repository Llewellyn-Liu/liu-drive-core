package com.lrl.liudrivecore.service.location;

public abstract class SaveConfiguration {

    public String method;

    public Integer compressed;

    public String token;

    public SaveConfiguration(){

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
}
