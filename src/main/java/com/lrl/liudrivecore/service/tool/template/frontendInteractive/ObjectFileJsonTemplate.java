package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ObjectFileJsonTemplate {

    private String filename;

    private String userId;

    private String type;

    private Integer accessibility;

    private ArrayList<String> tags;

    private ZonedDateTime dateCreated;

    private String url;


    public ObjectFileJsonTemplate() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ObjectFileMeta getObjectFileMeta(){
        ObjectFileMeta meta = new ObjectFileMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(ZonedDateTime.now());
        meta.setUserId(userId);
        if(tags!=null){
            String tagStr = "";
            for(String t: tags){
                tagStr+=t;
                tagStr+=";";
            }
            meta.setTags(tagStr.substring(0, tagStr.length()-1));
        }

        return meta;
    }

    @Override
    public String toString() {
        return "ObjectFileJsonTemplate{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tags=" + tags +
                ", dateCreated=" + dateCreated +
                ", url='" + url + '\'' +
                '}';
    }
}
