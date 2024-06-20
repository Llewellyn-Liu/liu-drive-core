package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;

public class ObjectSchemaBuilder<S extends ObjectMeta,T extends DefaultSaveConfigurationImpl> extends SchemaBuilderImpl<ObjectDTO<S,T>>{

    @Override
    public String buildUrl(ObjectDTO<S, T> objectDTO, String pathPreference) {
        // If url is not null, assume the url is good
        if (objectDTO.getUrl() != null) return objectDTO.getUrl();

        // Check if preference is bad
        if (pathPreference == null || !pathPreference.startsWith(objectDTO.getMeta().getUserId())) {
            pathPreference = objectDTO.getMeta().getUserId();
        }

        pathPreference.replace('\\', '/');

        // Remove "/" in the tails
        if (pathPreference.endsWith("/")) pathPreference = pathPreference.substring(0, pathPreference.length() - 1);
        if(pathPreference.endsWith("/")) pathPreference = objectDTO.getMeta().getUserId();

        StringBuilder sb = new StringBuilder();
        sb.append("/");

        // Accept filename as alternative for the image name
        if (objectDTO.getMeta().getFilename() == null) {
            String urlBuild = sb.append("" + System.currentTimeMillis()).toString();
            objectDTO.setUrl(urlBuild);
            return urlBuild;
        }


        // final - if no condition fits
        String res = sb.append(objectDTO.getMeta().getFilename()).toString();
        objectDTO.setUrl(res);

        return res;
    }

}
