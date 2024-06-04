package com.lrl.liudrivecore.data.pojo;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("Memo")
public class  MemoBlock extends Meta{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Integer accessibility;

    private String userId;

    private List<String> tags;

    private String dateCreated;


    private Map<String, Object> data;

    private String etag;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Integer accessibility) {
        this.accessibility = accessibility;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String eTag) {
        this.etag = eTag;
    }

    @Override
    public String toString() {
        return "MemoBlock{" +
                "id='" + id + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", data='" + data + '\'' +
                ", etag='" + etag + '\'' +
                '}';
    }
}
