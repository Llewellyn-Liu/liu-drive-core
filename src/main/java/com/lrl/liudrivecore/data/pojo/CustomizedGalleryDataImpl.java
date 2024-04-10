package com.lrl.liudrivecore.data.pojo;

public class CustomizedGalleryDataImpl implements CustomizedGalleryData{

    private String filename;
    private String url;

    private String userId;
    private String tags;

    private String author;

    private String dataCreated;

    private Integer accessibility;

    private String type;

    private Integer scale;

    public CustomizedGalleryDataImpl() {
    }

    public CustomizedGalleryDataImpl(String filename, String url, String userId, String tags, String author, String dataCreated, Integer accessibility, String type, Integer scale) {
        this.filename = filename;
        this.url = url;
        this.userId = userId;
        this.tags = tags;
        this.author = author;
        this.dataCreated = dataCreated;
        this.accessibility = accessibility;
        this.type = type;
        this.scale = scale;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDataCreated(String dataCreated) {
        this.dataCreated = dataCreated;
    }

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @Override
    public Integer getAccessibility() {
        return accessibility;
    }

    @Override
    public String getDateCreated() {
        return dataCreated;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getTags() {
        return tags;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Integer getScale() {
        return scale;
    }
}
