package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * If any condition doesn't meet, the url is set as userId
 */
@Component
public class DefaultImageSchemaBuilder extends ImageSchemaBuilder<ImageMeta, ImageSaveConfiguration>{



}
