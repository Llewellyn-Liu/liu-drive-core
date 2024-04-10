package com.lrl.liudrivecore.service.tool.template;

import com.lrl.liudrivecore.data.pojo.ImageMeta;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ImageFileBase64 {

    private String filename;

    private String userId;

    // Data format: "data:MIMETYPE;base64,DATA"
    // Eg: data:image/jpeg;base64,/9j/4AAQSkZJRgABAQA...
    private String data;

    private String type;

    private Integer accessibility;

    private ZonedDateTime dateCreated;

    private List<String> tag = new ArrayList<>();

    private String author;

    private Integer scale;


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

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
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

    public ImageMeta getMeta(){

        ImageMeta meta = new ImageMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(dateCreated);
        meta.setUserId(userId);
        meta.setScale(scale);
        meta.setAuthor(author);

        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) outputTags.append(s);
        meta.setTags(outputTags.toString());

        meta.setType(getMediaType(data));

        return meta;
    }

    private String getMediaType(String data) {

        return data.split(";")[0].split(":")[1];
    }

    public static ImageFileBase64 copy(ImageMeta meta, byte[] data){
        ImageFileBase64 file = new ImageFileBase64();
        file.setFilename(meta.getFilename());
        file.setAccessibility(meta.getAccessibility());
        file.setType(meta.getType());
        file.setDateCreated(meta.getDateCreated());
        file.setUserId(meta.getUserId());

        if(meta.getTags()!=null){
            String[] tags = meta.getTags().split(";");
            for(String s: tags) file.tag.add(s);

        }

        file.setData("data:"+meta.getType()+";base64,"+Base64.getEncoder().encodeToString(data));

        return file;
    }

    @Override
    public String toString() {
        return "ImageFileBase64{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", dateCreated=" + dateCreated +
                ", tag=" + tag +
                ", author='" + author + '\'' +
                ", scale=" + scale +
                '}';
    }
}
