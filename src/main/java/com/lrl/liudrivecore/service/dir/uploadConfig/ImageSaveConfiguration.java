package com.lrl.liudrivecore.service.dir.uploadConfig;

public class ImageSaveConfiguration extends DefaultSaveConfigurationImpl {

    protected Integer scale;

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public ImageSaveConfiguration() {

    }

    @Override
    public String toString() {
        return "ImageSaveConfiguration{" +
                "scale=" + scale +
                ", accessibility=" + accessibility +
                ", drive='" + drive + '\'' +
                ", compressed='" + compressed + '\'' +
                ", acl=" + acl +
                ", token='" + token + '\'' +
                ", accessKey='" + accessKey + '\'' +
                '}';
    }
}