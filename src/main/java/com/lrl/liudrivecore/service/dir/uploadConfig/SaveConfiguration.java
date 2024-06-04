package com.lrl.liudrivecore.service.dir.uploadConfig;

import java.util.List;

public abstract class SaveConfiguration {

    /**
     * v0.1.5
     * 0 for public, 1 for private, 2 for encrypted
     */
    protected int accessibility;


    /**
     * Drive location: "default" and "local" => "local"
     * "cloud" => "cloud"
     */
    protected String drive;

    /**
     * Compressed: "default" => not compressed
     * "gzip" => gzip compressed
     */
    protected String compressed;

    /**
     * Unimplemented
     */
    protected List<String> acl;

    /**
     * Access token for encrypted resources
     */
    protected String token;

    /**
     * Access token for cloud resources
     */
    protected String accessKey;

    public SaveConfiguration(){

    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getCompressed() {
        return compressed;
    }

    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public List<String> getAcl() {
        return acl;
    }

    public void setAcl(List<String> acl) {
        this.acl = acl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}
