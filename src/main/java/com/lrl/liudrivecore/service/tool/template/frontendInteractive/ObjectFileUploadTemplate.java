package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import com.lrl.liudrivecore.service.tool.template.ObjectFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ObjectFileUploadTemplate {

    private String filename;

    private String userId;

    private String data;

    private String type;

    private Integer accessibility;

    private ArrayList<String> tag;

    private String token;


    public ObjectFileUploadTemplate() {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ObjectFile getObjectFile(){
        ObjectFile objectFile = new ObjectFile();
        objectFile.setFilename(filename);
        objectFile.setData(data.getBytes());
        objectFile.setAccessibility(accessibility);
        objectFile.setType(type);
        objectFile.setTag(tag);
        objectFile.setUserId(userId);

        return objectFile;
    }

    public ObjectFileMeta getObjectFileMeta(){
        ObjectFileMeta meta = new ObjectFileMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(ZonedDateTime.now());
        meta.setUserId(userId);
        if(tag!=null){
            String tags = "";
            for(String t: tag){
                tags+=t;
                tags+=";";
            }
            meta.setTags(tags.substring(0, tags.length()-1));
        }

        return meta;
    }

    @Override
    public String toString() {
        return "ObjectFileTemplate{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", data=" + data +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tag=" + tag +
                '}';
    }
}
