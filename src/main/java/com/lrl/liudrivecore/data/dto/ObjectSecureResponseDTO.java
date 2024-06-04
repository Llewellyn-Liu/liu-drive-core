package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescriptionSimpleRecord;
import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Outward only DTO
 */
public class ObjectSecureResponseDTO{


    protected String url;

    protected ObjectMetaExposureDTO meta;

    protected SaveConfigurationExposureDTO config;

    protected List<String> tags;
    protected List<FileDescriptionSimpleRecord> sub;

    protected String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ObjectMetaExposureDTO getMeta() {
        return meta;
    }

    public SaveConfigurationExposureDTO getConfig() {
        return config;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FileDescriptionSimpleRecord> getSub() {
        return sub;
    }

    public void setSub(List<FileDescriptionSimpleRecord> sub) {
        this.sub = sub;
    }

    public void setConfig(DefaultSaveConfigurationImpl config) {
        if(config == null) return;

        this.config = new SaveConfigurationExposureDTO();
        this.getConfig().setDrive(config.getDrive());
        this.getConfig().setAccessibility(config.getAccessibility());
        this.getConfig().setCompressed(config.getCompressed());
    }

    public void setMeta(ObjectMeta meta) {
        if(meta == null) return;

        this.meta = new ObjectMetaExposureDTO();
        this.getMeta().setUserId(meta.getUserId());
        this.getMeta().setEtag(meta.getEtag());
        this.getMeta().setAuthor(meta.getAuthor());
        this.getMeta().setFilename(meta.getFilename());
        this.getMeta().setDateCreated(meta.getDateCreated());
        this.getMeta().setLastModified(meta.getLastModified());
        this.getMeta().setMimeType(meta.getMimeType());
    }

    public static ObjectSecureResponseDTO secureCopy(FileDescription source){
        ObjectSecureResponseDTO self = new ObjectSecureResponseDTO();
        self.setUrl(source.getUrl());
        self.setMeta(source.getMeta());
        self.setConfig((DefaultSaveConfigurationImpl) source.getConfig());
        self.setType(source.getType());
        self.setTags(source.getTags());

        // copy sub records for directories
        List<FileDescriptionSimpleRecord> subs = new LinkedList<>();
        for(FileDescriptionSimpleRecord fd: source.getSub()) subs.add(fd);
        self.setSub(subs);
        return self;
    }

    public static ObjectSecureResponseDTO secureCopy(ImageDescription source){
        ObjectSecureResponseDTO self = new ObjectSecureResponseDTO();
        self.setUrl(source.getUrl());
        self.setMeta(source.getMeta());
        self.setConfig((DefaultSaveConfigurationImpl) source.getConfig());
        self.setTags(source.getTags());
        return self;
    }
}
