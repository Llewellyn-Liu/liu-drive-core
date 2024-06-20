package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultImageSchemaValidator extends ImageSchemaValidator<ImageMeta, ImageSaveConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(DefaultImageSchemaValidator.class);

    @Override
    public ImageDTO<ImageMeta, ImageSaveConfiguration> isProtocolValid(ImageDTO<ImageMeta, ImageSaveConfiguration> stImageDTO, String path, boolean isEnforced) {

        if (!isEnforced) return isProtocolValidFuseCheck(stImageDTO, path) ? stImageDTO : null;

        filter(stImageDTO);

        // if the dto is meaningless, I am speechless
        if (!isValid(stImageDTO)) return null;

        // JSON.url is allowed to be null
        if (stImageDTO.getUrl() == null && (path == null || path.equals(""))) {
            // If all empty, under isEnforce == true, the dto is ready for build()
            return stImageDTO;
        }

        // Path check
        if (path != null) {
            String[] pathElements = path.split("/");
            if (pathElements.length > 2 || !pathElements[0].equals(stImageDTO.getMeta().getUserId())) return null;
            if (pathElements.length == 2 && !pathElements[1].equals(stImageDTO.getMeta().getFilename())) return null;
        }

        return stImageDTO;
    }

    @Override
    public boolean isValid(ImageDTO<ImageMeta, ImageSaveConfiguration> stImageDTO) {
        // Type check. Type should not be null
        if (stImageDTO.getType() == null || !stImageDTO.getType().equals("object"))
            return false;

        if (stImageDTO.getMeta() != null) {
            if (stImageDTO.getMeta().getEtag() != null) return false;
            if (stImageDTO.getMeta().getLocation() != null) return false;
            if (stImageDTO.getMeta().getDateCreated() != null) return false;
            if (stImageDTO.getMeta().getLastModified() != null) return false;

            if (stImageDTO.getMeta().getUserId() == null) return false;
            if (stImageDTO.getMeta().getFilename() == null) return false;
        }

        if (stImageDTO.getConfig() != null) {
            DefaultSaveConfigurationImpl config = stImageDTO.getConfig();
            if (config.getAccessibility() > 2) return false;
            if (config.getDrive() != null
                    && !config.getDrive().equals("default")
                    && !config.getDrive().equals("local")
                    && !config.getDrive().equals("cloud")) {
                return false;
            }
        }

        if (stImageDTO.getUrl() != null) {
            stImageDTO.setUrl(stImageDTO.getUrl().replace('\\', '/'));

            String[] urlElements = stImageDTO.getUrl().split("/");

            // url element size must == 2
            if (urlElements.length != 2) return false;
            // Url root must be userId
            if (!urlElements[0].equals(stImageDTO.getMeta().getUserId())) return false;
            // Fort objects, url last element must equal to filename
            if (!urlElements[urlElements.length - 1].equals(stImageDTO.getMeta().getFilename()))
                return false;
        }

        return true;
    }

    @Override
    public void filter(ImageDTO<ImageMeta, ImageSaveConfiguration> stImageDTO) {
        if (stImageDTO.getMeta() != null) {
            ImageMeta m = stImageDTO.getMeta();
            m.setEtag(null);
            m.setLocation(null);
            m.setDateCreated(null);
            m.setLastModified(null);
        }
    }

    /**
     * DTO check under isEnforced == false mode.
     * Strict check used
     *
     * @param imageDTO
     * @param path
     * @return
     */
    public boolean isProtocolValidFuseCheck(ImageDTO<ImageMeta, ImageSaveConfiguration> imageDTO, String path) {

        if (path == null || path.equals("")) return false;

        // JSON.url is allowed to be null
        if (imageDTO.getUrl() != null && !imageDTO.getUrl().equals(path)) {
            return false;
        }

        filter(imageDTO);

        if (!isValid(imageDTO)) return false;

        // Path check
        String[] pathElements = path.split("/");
        if (pathElements.length != 2 || !pathElements[0].equals(imageDTO.getMeta().getUserId())) return false;
        if (!pathElements[1].equals(imageDTO.getMeta().getFilename())) return false;


        return true;
    }

}
