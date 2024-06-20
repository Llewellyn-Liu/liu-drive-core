package com.lrl.liudrivecore.data.dto;

import com.lrl.liudrivecore.data.pojo.mongo.AbstractMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

public class ObjectDTO<S extends ObjectMeta, T extends DefaultSaveConfigurationImpl> extends AbstractObjectDTO<S, T> {

    public ObjectDTO(){

    }

    @Override
    public String toString() {
        return "ObjectDTO{" +
                "url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                '}';
    }
}
