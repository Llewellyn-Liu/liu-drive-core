package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.AudioMeta;

import java.time.ZonedDateTime;
import java.util.List;

public class AudioMetaJsonTemplate {

    private String filename;

    private String userId;

    private String type;

    private Integer accessibility;

    private List<String> tags;

    private ZonedDateTime dateCreated;

    private String url;

    private String author;


    public AudioMetaJsonTemplate() {
    }


    public AudioMetaJsonTemplate(AudioMeta meta) {
        this.filename = meta.getFilename();
        this.userId = meta.getUserId();
        this.type = meta.getType();
        this.accessibility = meta.getAccessibility();
        this.dateCreated = meta.getDateCreated();
        this.url= meta.getUrl();
        this.author = meta.getAuthor();
        if(meta.getTags()!=null){
            this.tags = List.of(meta.getTags().split(";"));
        }
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public AudioMeta getMeta(){
        AudioMeta meta = new AudioMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(ZonedDateTime.now());
        meta.setUserId(userId);
        meta.setAuthor(author);
        meta.setUrl(url);
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
        return "AudioFileJsonTemplate{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tags=" + tags +
                ", dateCreated=" + dateCreated +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
