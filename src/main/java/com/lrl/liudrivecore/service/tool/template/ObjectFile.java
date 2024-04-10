package com.lrl.liudrivecore.service.tool.template;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectFile {

    private String filename;

    private String userId;

    private byte[] data;

    private String type;

    private Integer accessibility;

    private ZonedDateTime dateCreated;

    private List<String> tag = new ArrayList<>();


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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
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


    public ObjectFileMeta getMeta(){

        ObjectFileMeta meta = new ObjectFileMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(dateCreated);
        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) outputTags.append(s+";");
        meta.setTags(outputTags.toString());
        meta.setUserId(userId);

        System.out.println(meta);

        return meta;
    }

    public static ObjectFile copy(ObjectFileMeta meta, byte[] data){
        ObjectFile file = new ObjectFile();
        file.setFilename(meta.getFilename());
        file.setAccessibility(meta.getAccessibility());
        file.setType(meta.getType());
        file.setDateCreated(meta.getDateCreated());

        if(meta.getTags()!=null){
            String[] tags = meta.getTags().split(";");
            for(String s: tags) file.tag.add(s);

        }
        file.setUserId(meta.getUserId());

        file.setData(data);

        return file;
    }

    @Override
    public String toString() {
        return "ObjectFile{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", data=" + Arrays.toString(data) +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", dateCreated=" + dateCreated +
                ", tag=" + tag +
                '}';
    }


}
