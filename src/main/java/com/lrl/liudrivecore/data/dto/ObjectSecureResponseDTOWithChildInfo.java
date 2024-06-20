package com.lrl.liudrivecore.data.dto;

import java.util.LinkedList;
import java.util.List;

public class ObjectSecureResponseDTOWithChildInfo extends ObjectSecureResponseDTO{
    
    public ObjectSecureResponseDTOWithChildInfo(ObjectSecureResponseDTO o){
        copyFrom(o);
        this.children = new LinkedList<>();
    }

    protected List<ObjectSecureResponseDTO> children;

    public List<ObjectSecureResponseDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ObjectSecureResponseDTO> children) {
        this.children = children;
    }

    public void addChild(ObjectSecureResponseDTO child) {
        this.children.add(child);
    }

    private void copyFrom(ObjectSecureResponseDTO o) {
        this.setMeta(o.getMeta());
        this.setConfig(o.getConfig());
        this.setSub(o.getSub());
        this.setUrl(o.getUrl());
        this.setTags(o.getTags());
        this.setType(o.getType());
    }
}
