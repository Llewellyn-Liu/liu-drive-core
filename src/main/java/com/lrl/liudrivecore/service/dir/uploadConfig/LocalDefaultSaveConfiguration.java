package com.lrl.liudrivecore.service.dir.uploadConfig;

public class LocalDefaultSaveConfiguration extends SaveConfiguration {

    private static LocalDefaultSaveConfiguration instance = new LocalDefaultSaveConfiguration();;

    public LocalDefaultSaveConfiguration() {

        this.drive = "local";
        this.compressed = "default";
    }

    public static LocalDefaultSaveConfiguration getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "DefaultSaveConfiguration{" +
                "accessibility=" + accessibility +
                ", drive='" + drive + '\'' +
                ", compressed='" + compressed + '\'' +
                ", acl=" + acl +
                ", token='" + token + '\'' +
                '}';
    }
}