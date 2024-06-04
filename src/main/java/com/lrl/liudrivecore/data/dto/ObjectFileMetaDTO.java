package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;

import java.time.ZonedDateTime;
import java.util.List;

public class ObjectFileMetaDTO {

    private Integer accessibility;

    private String userId;

    private List<String> tags;

    private String type;

    private String filename;

    private String url;

    private ZonedDateTime dateCreated;

    private String token;


    public ObjectFileMetaDTO(){

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

    public ObjectFileMeta getMeta(){

        ObjectFileMeta meta = new ObjectFileMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setUrl(url);
        meta.setDateCreated(ZonedDateTime.now());
        meta.setUserId(userId);
        if(tags!=null && tags.size() != 0){
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
        return "ObjectFileMetaDTO{" +
                "accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                ", dateCreated=" + dateCreated +
                ", token='" + token + '\'' +
                '}';
    }
}
