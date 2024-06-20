package com.lrl.liudrivecore.data.dto.schema;


import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.Memo;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * Added isEnforced and isDynamic to fit circumstances in POST, PUT, PATCH
 * isEnforced:
 *      if the method should attach good url to the dto. If false, null is returned
 * isDynamic:
 *      if the method should generate different url for the same structure and content,
 *      For PUT, this enables idempotency.
 *
 * default:
 * POST: isEnforced -> true, isDynamic -> true
 * PUT: isEnforced -> true, isDynamic -> false
 * PATCH: isEnforced -> true, isDynamic -> true
 *
 * APIs can set isEnforced as false to avoid bad value being saved
 */
@Component
public class Schema {

    private DefaultImageSchemaValidator imageSchemaValidator;

    private DefaultObjectSchemaValidator objectSchemaValidator;

    private DefaultImageSchemaBuilder imageSchemaBuilder;

    private DefaultObjectSchemaBuilder objectSchemaBuilder;

    private MemoValidator memoValidator;


    public Schema() {
        this.imageSchemaBuilder = new DefaultImageSchemaBuilder();
        this.objectSchemaBuilder = new DefaultObjectSchemaBuilder();
        this.imageSchemaValidator = new DefaultImageSchemaValidator();
        this.objectSchemaValidator = new DefaultObjectSchemaValidator();
        this.memoValidator = new MemoValidator();

    }

    /**
     *
     * @param imageDTO
     * @param isEnforced If true, filter will try to generate good value for dtos, otherwise give up
     * @return
     */
    public ImageDTO filterImageDTO(ImageDTO imageDTO, String path, boolean isEnforced){

        imageDTO = imageSchemaValidator.isProtocolValid(imageDTO, path, isEnforced);
        if(imageDTO == null){
             return null;
        }

        imageSchemaBuilder.buildUrl(imageDTO, path != null ? path : imageDTO.getMeta().getUserId()+"/"+imageDTO.getMeta().getFilename());
        imageSchemaBuilder.buildLocation(imageDTO);
        return imageDTO;
    }

    public ObjectDTO filterObjectDTO(ObjectDTO objectDTO, String path, boolean isEnforced, HttpMethod method){
        objectDTO = objectSchemaValidator.isProtocolValid(objectDTO, path, isEnforced, method);
        if(objectDTO == null){
            return null;
        }

        objectSchemaBuilder.buildUrl(objectDTO, path != null ? path : objectDTO.getMeta().getUserId()+"/"+objectDTO.getMeta().getFilename());
        objectSchemaBuilder.buildLocation(objectDTO);
        return objectDTO;
    }

    public Memo filterMemo(Memo memo, String path, boolean isEnforced, HttpMethod method) {

        memo = memoValidator.isProtocolValid(memo, path, isEnforced, method);
        if(memo == null) return null;

        // Build url
        if(isEnforced && memo.getUrl() == null) memo.setUrl(memo.getUserId()+"/"+System.currentTimeMillis());
        return memo;
    }

}
