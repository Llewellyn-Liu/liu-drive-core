package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import com.lrl.liudrivecore.service.util.record.ObjectRecord;

import java.util.Base64;

/**
 * Outward only DTO
 */
public class ObjectSecureResponseDTOWithData extends ObjectSecureResponseDTO{

    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static ObjectSecureResponseDTOWithData secureCopy(ObjectRecord or){
        ObjectSecureResponseDTOWithData target = new ObjectSecureResponseDTOWithData();
        ObjectSecureResponseDTO source = or.description();
        target.setData(Base64.getEncoder().encodeToString(or.data()));
        target.setConfig(source.getConfig());

        target.setMeta(source.getMeta());
        target.setUrl(source.getUrl());
        target.setTags(source.getTags());

        return target;
    }

    @Override
    public String toString() {
        return "ObjectSecureResponseDTOWithData{" +
                "data='" + data + '\'' +
                ", url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", sub=" + sub +
                ", type='" + type + '\'' +
                '}';
    }
}
