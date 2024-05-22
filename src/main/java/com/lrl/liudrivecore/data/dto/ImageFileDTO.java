package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.service.location.DefaultSaveConfiguration;

import java.util.List;

public class ImageFileDTO {

    String filename;

    String userId;

    String type;

    String url;

    Integer accessibility;

    List<String> tags;

    Integer scale;

    String author;

    String token;

    byte[] data;

    private DefaultSaveConfiguration configuration;

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

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public DefaultSaveConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DefaultSaveConfiguration configuration) {
        this.configuration = configuration;
    }

    public ImageMeta getMeta() {

        ImageMeta meta = new ImageMeta();
        meta.setFilename(filename);
        meta.setUrl(url);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        StringBuffer plainTags = new StringBuffer();
        for (String tag : tags) {
            plainTags.append(tag);
            plainTags.append(";");
        }
        meta.setTags(plainTags.substring(0, plainTags.length() - 1));
        meta.setUserId(userId);
        meta.setAuthor(author);
        meta.setScale(scale);
        // No setToken needed.
        return meta;

    }

    @Override
    public String toString() {
        return "ImageMetaDTO{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", accessibility=" + accessibility +
                ", tag=" + tags +
                ", scale=" + scale +
                ", author='" + author + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
