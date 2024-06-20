package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import org.springframework.http.HttpMethod;

public abstract class ObjectSchemaValidator<S extends ObjectMeta, T extends DefaultSaveConfigurationImpl> implements SchemaValidator<ObjectDTO<S,T>> {

    /**
     * Check if dtos fields are correctly set
     * isValid() promises necessary information is included, which enables builder to build fields.
     * @param stObjectDTO
     * @return
     */
    @Override
    public abstract boolean isValid(ObjectDTO<S, T> stObjectDTO);


    /**
     * Check if dtos contain not allowed fields(tool fields)
     * Filter doesn't prevent bat input for use case specific forbidden fields(e.g. type for PUT)
     * @param stObjectDTO
     */
    public abstract void filter(ObjectDTO<S, T> stObjectDTO);

    /**
     * Should provide capability of isValid() method, and compare if a provided path is also valid
     * If isEnforced is false, when bad value is found, fuse and return null
     * Otherwise, run full field check
     * @param stObjectDTO
     * @param path
     * @param isEnforced If true, run full check and the bad value is covered
     * @return
     */
    public abstract ObjectDTO<S, T>  isProtocolValid(ObjectDTO<S, T> stObjectDTO, String path, boolean isEnforced, HttpMethod method);

}
