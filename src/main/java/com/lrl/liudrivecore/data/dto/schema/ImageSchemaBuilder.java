package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.AbstractObjectDTO;
import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;

/**
 * Provide customization for build url and build location function
 * @param <S>
 * @param <T>
 */
public class ImageSchemaBuilder<S extends ImageMeta, T extends ImageSaveConfiguration> extends SchemaBuilderImpl<ImageDTO<S,T>>{

    @Override
    public String buildUrl(ImageDTO<S, T> imageDTO, String pathPreference) {
        // If url is not null, assume the url is good
        if (imageDTO.getUrl() != null) return imageDTO.getUrl();
        else{
            String pattern =imageDTO.getMeta().getUserId() + "/" + imageDTO.getMeta().getFilename();
            imageDTO.setUrl(pattern);
            return pattern;
        }
    }


}
