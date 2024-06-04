package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

import java.util.List;

public class ObjectDTO {

    protected String url;

    protected ObjectMeta meta;

    protected DefaultSaveConfigurationImpl config;

    protected List<String> tags;

    protected String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ObjectMeta getMeta() {
        return meta;
    }

    public void setMeta(ObjectMeta meta) {
        this.meta = meta;
    }

    public SaveConfiguration getConfig() {
        return config;
    }

    public void setConfig(DefaultSaveConfigurationImpl config) {
        this.config = config;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ObjectDTO{" +
                "url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", type=" + type +
                ", tags=" + tags +
                '}';
    }
}
