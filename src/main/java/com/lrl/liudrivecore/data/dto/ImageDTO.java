package com.lrl.liudrivecore.data.dto;

public class ImageDTO extends ObjectDTO{

    @Override
    public String toString() {
        return "ImageDTO{" +
                "url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                '}';
    }
}
