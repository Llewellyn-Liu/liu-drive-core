package com.lrl.liudrivecore.service.tool.template;

import com.lrl.liudrivecore.data.pojo.ImageMeta;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ImageFile {

    private String filename;

    private String userId;

    private byte[] data;

    private String type;

    private Integer accessibility;

    private ZonedDateTime dateCreated;

    private ArrayList<String> tag = new ArrayList<>();

    private MediaType mediaType;
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

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public ImageMeta getMeta(){

        ImageMeta meta = new ImageMeta();
        meta.setFilename(filename);
        meta.setAccessibility(accessibility);
        meta.setType(type);
        meta.setDateCreated(dateCreated);
        StringBuffer outputTags = new StringBuffer();
        for(String s: tag) {
            outputTags.append(s);
            outputTags.append(";");
        }
        meta.setTags(outputTags.substring(0, outputTags.length()-1));
        meta.setUserId(userId);
        meta.setScale(scale);

        return meta;
    }

    public static ImageFile copy(ImageMeta meta, byte[] data){
        ImageFile file = new ImageFile();
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
        file.setMediaType(MediaType.asMediaType(MimeType.valueOf(meta.getType())));

        return file;
    }

    @Override
    public String toString() {
        return "ObjectFile{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", data=" + data +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", dateCreated=" + dateCreated +
                ", tag=" + tag +
                '}';
    }


}
