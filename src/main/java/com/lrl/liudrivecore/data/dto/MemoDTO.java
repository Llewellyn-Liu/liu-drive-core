package com.lrl.liudrivecore.data.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.pojo.mongo.Memo;
import org.bson.types.Binary;

public class MemoDTO {

    protected Object data;

    protected String userId;

    protected String url;

    private ObjectMapper mapper;

    public MemoDTO() {
        mapper = new ObjectMapper();
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

    @Override
    public String toString() {
        return "MemoDTO{" +
                "data=" + data +
                ", userId='" + userId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Memo getMemo() {
        Memo memo = new Memo();
        memo.setUrl(url);

        byte[] d = null;
        try {
             d = mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(d == null) return null;

        memo.setData(new Binary(d));
        memo.setUserId(userId);
        return memo;
    }
}
