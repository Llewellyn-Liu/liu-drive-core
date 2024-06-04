package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.LocalDefaultSaveConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class ObjectFileDTO {

    private Integer accessibility;

    private String userId;

    private List<String> tags;

    private String type;

    private String filename;

    private String url;

    private ZonedDateTime dateCreated;

    private String token;

    private byte[] data;

    private LocalDefaultSaveConfiguration configuration;


    public ObjectFileDTO(){

    }

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public LocalDefaultSaveConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(LocalDefaultSaveConfiguration configuration) {
        this.configuration = configuration;
    }

    public ObjectFileMeta getMeta(){

        ObjectFileMeta meta = new ObjectFileMeta();
        meta.setFilename(filename);
        meta.setUrl(url);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(ZonedDateTime.now());
        meta.setUserId(userId);
        if(tags!=null){
            String plainTags = "";
            for(String t: tags){
                plainTags+=t;
                plainTags+=";";
            }
            meta.setTags(plainTags.substring(0, plainTags.length()-1));
        }

        return meta;
    }

    @Override
    public String toString() {
        return "ObjectFileDTO{" +
                "accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                ", dateCreated=" + dateCreated +
                ", token='" + token + '\'' +
                ", data=" + Arrays.toString(data) +
                ", configuration=" + configuration +
                '}';
    }
}
