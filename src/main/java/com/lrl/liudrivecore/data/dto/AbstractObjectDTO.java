package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.mongo.AbstractMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

import java.util.List;

public abstract class AbstractObjectDTO<S extends AbstractMeta, T extends SaveConfiguration>{

    protected String url;

    protected S meta;

    protected T config;

    protected List<String> tags;

    protected String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public S getMeta() {
        return meta;
    }

    public void setMeta(S meta) {
        this.meta = meta;
    }

    public T getConfig() {
        return config;
    }

    public void setConfig(T config) {
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

}
