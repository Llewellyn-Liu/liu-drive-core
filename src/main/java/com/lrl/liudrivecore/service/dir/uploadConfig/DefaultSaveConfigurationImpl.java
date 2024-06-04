package com.lrl.liudrivecore.service.dir.uploadConfig;

public class DefaultSaveConfigurationImpl extends SaveConfiguration {


    public DefaultSaveConfigurationImpl() {

    }

    @Override
    public String toString() {
        return "DefaultSaveConfigurationImpl{" +
                "accessibility=" + accessibility +
                ", drive='" + drive + '\'' +
                ", compressed='" + compressed + '\'' +
                ", acl=" + acl +
                ", token='" + token + '\'' +
                ", accessKey='" + accessKey + '\'' +
                '}';
    }
}