package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;

public abstract class ImageSchemaValidator<S extends ImageMeta, T extends ImageSaveConfiguration> implements SchemaValidator<ImageDTO<S,T>> {

    /**
     * Check if dtos fields are correctly set
     * isValid() promises necessary information is included, which enables builder to build fields.
     * @param stImageDTO
     * @return
     */
    @Override
    public abstract boolean isValid(ImageDTO<S, T> stImageDTO);

    /**
     * Check if dtos contain not allowed fields
     * @param stImageDTO
     */
    public abstract void filter(ImageDTO<S, T> stImageDTO);

    /**
     * Should provide capability of isValid() method, and compare if a provided path is also valid
     * If isEnforced is false, when bad value is found, fuse and return null
     * Otherwise, run full field check
     * @param stImageDTO
     * @param path
     * @param isEnforced If true, run full check and the bad value is covered
     * @return
     */
    public abstract ImageDTO<S, T>  isProtocolValid(ImageDTO<S, T> stImageDTO, String path, boolean isEnforced);


}
