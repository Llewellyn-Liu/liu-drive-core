package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.service.util.record.ObjectRecord;

import java.util.Base64;

/**
 * Outward only DTO
 */
public class ObjectSecureResponseDTOWithData extends ObjectSecureResponseDTO{

    ObjectSecureResponseDTO desc;

    String data;

    public ObjectSecureResponseDTO getDesc() {
        return desc;
    }

    public void setDesc(ObjectSecureResponseDTO desc) {
        this.desc = desc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static ObjectSecureResponseDTO secureCopy(ObjectRecord or){
        ObjectSecureResponseDTOWithData target = new ObjectSecureResponseDTOWithData();
        target.setDesc(or.description());
        target.setData(Base64.getEncoder().encodeToString(or.data()));
        return target;
    }

    @Override
    public String toString() {
        return "ObjectSecureResponseDTOWithData{" +
                "desc=" + desc +
                ", data='" + data + '\'' +
                '}';
    }
}
