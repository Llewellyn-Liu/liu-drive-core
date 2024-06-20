package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.ImageMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class DefaultObjectSchemaValidator extends ObjectSchemaValidator<ObjectMeta, DefaultSaveConfigurationImpl> {


    @Override
    public boolean isValid(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {

        // Type check. Type should not be null
        if (objectDTO.getType() == null || (!objectDTO.getType().equals("object") &&  !objectDTO.getType().equals("directory")))
            return false;

        if (objectDTO.getMeta() != null) {
            if (objectDTO.getMeta().getEtag() != null) return false;
            if (objectDTO.getMeta().getLocation() != null) return false;
            if (objectDTO.getMeta().getDateCreated() != null) return false;
            if (objectDTO.getMeta().getLastModified() != null) return false;

            if (objectDTO.getMeta().getUserId() == null) return false;
            if (objectDTO.getMeta().getFilename() == null) return false;
        }

        if (objectDTO.getConfig() != null) {
            DefaultSaveConfigurationImpl config = objectDTO.getConfig();
            if (config.getAccessibility() > 2) return false;
            if (config.getDrive() != null
                    && !config.getDrive().equals("default")
                    && !config.getDrive().equals("local")
                    && !config.getDrive().equals("cloud")) {
                return false;
            }
        }

        if (objectDTO.getUrl() != null) {
            objectDTO.setUrl(objectDTO.getUrl().replace('\\', '/'));

            String[] urlElements = objectDTO.getUrl().split("/");

            // url element size must <= 5 and >= 2
            if (urlElements.length > 5 || urlElements.length < 2) return false;
            // Url root must be userId
            if (!urlElements[0].equals(objectDTO.getMeta().getUserId())) return false;
            // Fort objects, url last element must equal to filename
            if (objectDTO.getType().equals("object")
                    && !urlElements[urlElements.length - 1].equals(objectDTO.getMeta().getFilename()))
                return false;
        }

        return true;
    }

    @Override
    public void filter(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {
        if (objectDTO.getMeta() != null) {
            ObjectMeta m = objectDTO.getMeta();
            m.setEtag(null);
            m.setLocation(null);
            m.setDateCreated(null);
            m.setLastModified(null);
        }

    }

    @Override
    public ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> isProtocolValid(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path, boolean isEnforced, HttpMethod method) {
        if (!isEnforced) return isProtocolValidFuseCheck(objectDTO, path, method) ? objectDTO : null;

        boolean isTolerablePath = false;
        if (method == HttpMethod.POST) {
            isTolerablePath = isPathGoodForPost(objectDTO, path);
        } else if (method == HttpMethod.PUT) {
            isTolerablePath = isPathGoodForPut(objectDTO, path);
        } else if (method == HttpMethod.PATCH) {
            isTolerablePath = isPathGoodForPatch(objectDTO, path);
        }

        if (!isTolerablePath) return null;

        filter(objectDTO);

        if(!isValid(objectDTO)) return null;

        return objectDTO;
    }

    /**
     * isEnforced == true
     *
     * @param objectDTO
     * @param path
     * @return
     */
    private boolean isPathGoodForPost(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {
        if (objectDTO.getUrl() == null && (path == null || path.equals(""))) {
            return false;
        }

        if (objectDTO.getUrl() == null) {

            String[] pathElements = path.split("/");
            // url element size must <= 5
            if (pathElements.length > 5) return false;
            // first element must be userId
            if (pathElements.length > 0 && !pathElements[0].equals(objectDTO.getMeta().getUserId())) return false;
        } else {
            if (path != null) {
                String[] urlElements = objectDTO.getUrl().split("/");
                String[] pathElements = path.split("/");

                if (urlElements.length <= pathElements.length || pathElements.length > 5) return false;
                for (int i = 0; i < pathElements.length; i++) {
                    if (!pathElements[i].equals(urlElements[i])) return false;
                }
            }
        }

        return true;
    }

    /**
     * isEnforced == true
     *
     * @param objectDTO
     * @param path
     * @return
     */
    private boolean isPathGoodForPut(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {
        if (objectDTO.getUrl() == null && (path == null || path.equals(""))) {
            return false;
        }

        if (objectDTO.getUrl() == null) {

            String[] pathElements = path.split("/");
            // url element size must <= 5 and >= 2
            if (pathElements.length > 5 || pathElements.length < 2) return false;
            // first element must be userId
            if (pathElements.length > 0 && !pathElements[0].equals(objectDTO.getMeta().getUserId())) return false;
        } else {
            if (path != null) {
                String[] urlElements = objectDTO.getUrl().split("/");
                String[] pathElements = path.split("/");

                if (urlElements.length < pathElements.length || pathElements.length > 5) return false;
                for (int i = 0; i < pathElements.length; i++) {
                    if (!pathElements[i].equals(urlElements[i])) return false;
                }
            }
        }

        return true;
    }

    /**
     * isEnforced == true
     *
     * @param objectDTO
     * @param path
     * @return
     */
    private boolean isPathGoodForPatch(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {
        return isPathGoodForPut(objectDTO, path);
    }


    public boolean isProtocolValidFuseCheck(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path, HttpMethod method) {

        // JSON.url is allowed to be null
        if (objectDTO.getUrl() == null) {
            if (path == null || path.equals("")) return false;
        } else {
            if (!objectDTO.getUrl().equals(path)) return false;
        }

        filter(objectDTO);

        if (!isValid(objectDTO)) return false;


        if(method == HttpMethod.POST){
            return fuseCheckPost(objectDTO, path);
        }else if(method == HttpMethod.PUT){
            return fuseCheckPut(objectDTO, path);
        }else if(method == HttpMethod.PATCH){
            return fuseCheckPatch(objectDTO, path);
        }


        return true;


    }

    private boolean fuseCheckPost(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {


        if(path == null || path.equals("") || objectDTO.getUrl() == null || objectDTO.equals("")) return false;
        String[] pathElements = path.split("/");
        String[] urlElements = objectDTO.getUrl().split("/");

        if(pathElements.length< 1 || pathElements.length > 4) return false;
        if(!pathElements[0].equals(objectDTO.getMeta().getUserId()) || pathElements.length != urlElements.length - 1) return false;
        for(int i = 0; i < pathElements.length; i ++){
            if(!pathElements[i].equals(urlElements[i])) return false;
        }
        if(!urlElements[urlElements.length-1].equals(objectDTO.getMeta().getFilename())) return false;

        return true;
    }

    private boolean fuseCheckPut(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {


        if(path == null || path.equals("") || objectDTO.getUrl() == null || objectDTO.equals("")) return false;
        if(path.equals( objectDTO.getUrl())) return false;

        String[] pathElements = path.split("/");
        if(pathElements.length < 2 || pathElements.length > 5) return false;
        if(pathElements[0].equals(objectDTO.getMeta().getUserId())) return false;
        if(objectDTO.getType().equals("object")
                && !pathElements[pathElements.length-1].equals(objectDTO.getMeta().getFilename())) return false;

        if(pathElements.length< 1 || pathElements.length > 4) return false;
        if(objectDTO.getType().equals("object") && !pathElements[pathElements.length - 1].equals(objectDTO.getMeta().getFilename())) return false;

        return true;
    }
    private boolean fuseCheckPatch(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path) {
        return fuseCheckPut(objectDTO, path);
    }

}
