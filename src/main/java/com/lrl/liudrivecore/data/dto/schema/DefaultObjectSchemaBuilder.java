package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import org.springframework.stereotype.Component;

/**
 * If any condition doesn't meet, the url is set as userId
 */
@Component
public class DefaultObjectSchemaBuilder extends ObjectSchemaBuilder<ObjectMeta, DefaultSaveConfigurationImpl>{


    @Override
    public String buildUrl(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String pathPreference) {
        return super.buildUrl(objectDTO, pathPreference);
    }

    public String buildUrl(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String pathPreference, boolean isDynamic) {
        return super.buildUrl(objectDTO, pathPreference);
    }
}
