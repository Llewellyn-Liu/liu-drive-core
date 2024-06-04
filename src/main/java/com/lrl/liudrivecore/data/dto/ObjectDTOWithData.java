package com.lrl.liudrivecore.data.dto;

import java.util.Base64;

public class ObjectDTOWithData extends ObjectDTO{

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectDTOWithData{" +
                "data='" + data + '\'' +
                ", url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                '}';
    }
}
