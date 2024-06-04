package com.lrl.liudrivecore.data.dto;

public class ImageBase64DTO extends ImageDTO{

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImageBase64DTO{" +
                "data='" + data + '\'' +
                ", url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                '}';
    }
}
