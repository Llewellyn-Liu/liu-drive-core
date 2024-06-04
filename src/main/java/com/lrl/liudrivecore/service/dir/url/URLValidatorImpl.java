package com.lrl.liudrivecore.service.dir.url;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reference: v0.1.5 API
 */
public class URLValidatorImpl implements URLValidator {

    private final static String DEFAULT_DRIVE = "local";


    /**
     * 检查步骤参考v0.1.5 API
     *
     * @param path
     * @param objectDTO
     * @param isPartial If PATH represents a part of the url (e.g. POST methods), or full url (in PUT methods)
     * @return
     */
    @Override
    public boolean isValid(String path, ObjectDTO objectDTO, boolean isPartial) {

        // JSOn.url is allowed to be null
        if (objectDTO.getUrl() == null && !(path != null || path.equals(""))) return true;

        String url = objectDTO.getUrl();
        if (isPartial) {
            // POST itself is meaningless
            if (path.equals(url)) return false;
        } else {
            // now it must equal to url
            if (!path.equals(url)) return false;
        }

        String[] pathElements = path.split("/");
        String[] urlElements = url.split("/");

        // url element size must <= 5 and >= 2
        if (urlElements.length > 5 || urlElements.length < 2) return false;

        // Path must be the prefix of url
        if ((isPartial && pathElements.length >= urlElements.length)
                || (!isPartial && pathElements.length > urlElements.length))
            return false;

        // Url root must be userId
        if (!urlElements[0].equals(objectDTO.getMeta().getUserId())) return false;
        // Url last element must equal to filename
        if (!urlElements[urlElements.length - 1].equals(objectDTO.getMeta().getFilename())) return false;
        // If path mismatches the order of url return false
        for (int i = 0; i < pathElements.length; i++) {
            if (!pathElements[i].equals(urlElements[i])) return false;
        }

        return true;

    }

    /**
     * 检查步骤参考v0.1.5 API
     *
     * @param path
     * @param imageDTO
     * @param isPost   If PATH represents a part of the url (e.g. POST methods), or full url (in PUT methods)
     * @return
     */
    @Override
    public boolean isValidImageUrl(String path, ImageDTO imageDTO, boolean isPost) {

        // JSON.url is a must for POSTing an image url, but not necessary for PUT
        if (imageDTO.getUrl() == null && !(path != null || path.equals(""))) return !isPost;

        String url = imageDTO.getUrl();
        Path p = Paths.get(url);
        // Format: userId + filename, must be 2
        if (p.getNameCount() != 2) return false;
        // Root must be userId
        if (!p.getParent().equals(imageDTO.getMeta().getUserId())) return false;

        // For POST, url must equals to path and POST check ends here
        if (isPost) return path.equals(imageDTO.getUrl());

        // now it must equal to url
        if (!path.equals(imageDTO.getUrl())) return false;

        return true;

    }

    @Override
    public boolean isValidDirectoryUrl(ObjectDTO objectDTO, String path, boolean isPartial) {

        // JSOn.url is allowed to be null
        if (objectDTO.getUrl() == null && !(path != null || path.equals(""))) return true;

        String url = objectDTO.getUrl();
        if (isPartial) {
            // POST itself is meaningless
            if (path.equals(url)) return false;
        } else {
            // now it must equal to url
            if (!path.equals(url)) return false;
        }

        String[] pathElements = path.split("/");
        String[] urlElements = url.split("/");

        // url element size must <= 5 and >= 2
        if (urlElements.length > 4 || urlElements.length < 2) return false;

        // Path must be the prefix of url
        if ((isPartial && pathElements.length >= urlElements.length)
                || (!isPartial && pathElements.length > urlElements.length))
            return false;
        // Url root must be userId
        if (!urlElements[0].equals(objectDTO.getMeta().getUserId())) return false;

        // If path mismatches the order of url return false
        for (int i = 0; i < pathElements.length; i++) {
            if (!pathElements[i].equals(urlElements[i])) return false;
        }

        return true;
    }

    /**
     * Precondition: validate() invoked
     *
     * @param objectDTO
     * @return
     */
    @Override
    public String buildUrl(ObjectDTO objectDTO, String validPath) {
        if (objectDTO.getUrl() != null) return objectDTO.getUrl();

        if(validPath.endsWith("/")) validPath = validPath.substring(0,validPath.length()-1);
        String res = validPath + "/" + objectDTO.getMeta().getFilename();
        objectDTO.setUrl(res);

        return res;
    }

    /**
     * Precondition: objectDTO.url is valid and not null
     *
     * @param objectDTO
     * @return
     */
    @Override
    public void buildLocation(ObjectDTO objectDTO) {

        SaveConfiguration config = objectDTO.getConfig();
        String builtLocation;
        if (config.getDrive().equalsIgnoreCase("default")) {
            builtLocation = DEFAULT_DRIVE + ";" + objectDTO.getUrl();

        } else if (config.getDrive().equalsIgnoreCase("local")) {
            builtLocation = "local;" + objectDTO.getUrl();
        } else if (config.getDrive().equalsIgnoreCase("cloud")) {
            builtLocation = "cloud;" + config.getToken() + ";" + objectDTO.getUrl();
        } else throw new RuntimeException("URLCheck: Not a valid method");

        objectDTO.getMeta().setLocation(builtLocation);
    }
}
