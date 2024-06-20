package com.lrl.liudrivecore.data.pojo.mongo;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user-memo")
public class Memo {

    String id;

    protected String etag;

    protected Binary data;

    protected String userId;

    protected String url;

    public Memo(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Binary getData() {
        return data;
    }

    public void setData(Binary data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "etag='" + etag + '\'' +
                ", data=" + data +
                ", userId='" + userId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
