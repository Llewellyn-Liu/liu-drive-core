package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

import com.lrl.liudrivecore.data.pojo.AudioMeta;
import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.VideoMeta;

import java.util.ArrayList;
import java.util.List;

public class MediaMetaTemplate {

    protected String filename;

    protected String userId;

    protected String type;

    protected Integer accessibility;

    protected List<String> tag;
    protected Integer scale;

    protected String author;

    protected String token;

    public MediaMetaTemplate() {
        tag = new ArrayList<>();
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

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public ImageMeta asImageMeta(){
        ImageMeta meta = new ImageMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) {
            outputTags.append(s);
            outputTags.append(";");
        }
        meta.setTags(outputTags.substring(0, outputTags.length()-1));
        meta.setUserId(userId);
        meta.setAuthor(author);

        return meta;
    }

    public VideoMeta asVideoMeta(){
        VideoMeta meta = new VideoMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) {
            outputTags.append(s);
            outputTags.append(";");
        }
        meta.setTags(outputTags.substring(0, outputTags.length()-1));
        meta.setUserId(userId);
        meta.setAuthor(author);

        return meta;
    }

    public AudioMeta asAudioMeta(){
        AudioMeta meta = new AudioMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) {
            outputTags.append(s);
            outputTags.append(";");
        }
        meta.setTags(outputTags.substring(0, outputTags.length()-1));
        meta.setUserId(userId);
        meta.setAuthor(author);

        return meta;
    }
    @Override
    public String toString() {
        return "MediaUploadTemplate{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tag=" + tag +
                ", scale=" + scale +
                ", author='" + author + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
