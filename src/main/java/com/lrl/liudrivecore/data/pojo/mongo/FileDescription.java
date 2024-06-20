package com.lrl.liudrivecore.data.pojo.mongo;


import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document("user-directory")
public class FileDescription {

    String id;

    List<FileDescriptionSimpleRecord> sub;

    String type;

    String url;

    List<String> tags;

    ObjectMeta meta;

    SaveConfiguration config;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FileDescriptionSimpleRecord> getSub() {
        return sub;
    }

    public void setSub(List<FileDescriptionSimpleRecord> sub) {
        this.sub = sub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ObjectMeta getMeta() {
        return meta;
    }

    public void setMeta(ObjectMeta meta) {
        this.meta = meta;
    }

    public SaveConfiguration getConfig() {
        return config;
    }

    public void setConfig(SaveConfiguration config) {
        this.config = config;
    }

    public static FileDescription createRoot(String userId){
        FileDescription rev = new FileDescription();
        rev.sub = new LinkedList<>();
        rev.tags = new LinkedList<>();
        rev.url = userId+"/";
        rev.type = "directory";

        return rev;
    }

    public static FileDescription createPath(String url){
        FileDescription rev = new FileDescription();
        rev.sub = new LinkedList<>();
        rev.tags = new LinkedList<>();
        rev.url = url;
        rev.type = "directory";

        return rev;
    }

    public FileDescriptionSimpleRecord getSimpleRecord(){
        FileDescriptionSimpleRecord record = new FileDescriptionSimpleRecord();
        record.setUrl(this.getUrl());
        record.setType(this.getType());
        if(this.getConfig()!= null) record.setAccessibility(this.getConfig().getAccessibility());
        return record;
    }

    @Override
    public String toString() {
        return "FileDescription{" +
                "id='" + id + '\'' +
                ", sub=" + sub +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", tags=" + tags +
                ", meta=" + meta +
                ", config=" + config +
                '}';
    }
}
