package com.lrl.liudrivecore.data.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.pojo.mongo.Memo;
import org.bson.types.Binary;

public class MemoResponseDTO {

    protected Object data;

    protected String userId;

    protected String url;

    protected String etag;


    public MemoResponseDTO() {
    }
    public MemoResponseDTO(Memo memo) {

        this.data = memo.getData().getData();
        this.url = memo.getUrl();
        this.userId = memo.getUserId();
        this.etag = memo.getEtag();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public String toString() {
        return "MemoResponseDTO{" +
                "data=" + data +
                ", userId='" + userId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
