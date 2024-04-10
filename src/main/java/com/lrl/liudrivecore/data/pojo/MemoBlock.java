package com.lrl.liudrivecore.data.pojo;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Memo")
public class MemoBlock extends Meta{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private Integer accessibility;

    private String userId;

    private String tags;

    private String dateCreated;
    private String title;

    private String content;


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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    @Override
    public String toString() {
        return "MemoBlock{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags=" + tags +
                ", timeCreated=" + dateCreated +
                '}';
    }
}
