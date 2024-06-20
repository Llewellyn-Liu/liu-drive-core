package com.lrl.liudrivecore.data.dto;


import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

import java.awt.*;

public class ImageDTO<S extends ImageMeta, T extends ImageSaveConfiguration> extends ObjectDTO<S,T>{



    @Override
    public String toString() {
        return "ImageDTO{" +
                ", url='" + url + '\'' +
                ", meta=" + meta +
                ", config=" + config +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                '}';
    }
}
