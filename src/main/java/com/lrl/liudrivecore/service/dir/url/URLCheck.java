package com.lrl.liudrivecore.service.dir.url;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.StructuredFileMeta;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;

public class URLCheck {

    private static Logger logger = LoggerFactory.getLogger(URLCheck.class);
    private final static String URL_SEPARATOR = "/";

    private final static String DEFAULT_DRIVE = "local";

    /**
     * Generate url which frontend can access the resource.
     * If user has provided an url as folder, set the filename after the folder.
     * Format: userId/folders.../filename
     * Eg.: test_id/folder1/test.txt
     *
     * @param meta
     * @return
     */
    public static String buildUrl(StructuredFileMeta meta, SaveConfiguration configuration) {
        if (!isValidCustomizedPathUrl(meta.getUrl(), meta.getUserId())) meta.setUrl(null);

        if (meta.getUrl() == null) {
            meta.setUrl(meta.getUserId() + URL_SEPARATOR + meta.getFilename());
        }

        meta.setLocation(buildLocation(configuration, meta.getUrl()));
        System.out.println("Debug: location: " + meta.getLocation());
        return meta.getUrl();
    }


    /**
     * @deprecated moved to URLValidator
     * Generate url which frontend can access the resource.
     * If user has provided an url as folder, set the filename after the folder.
     * Format: userId/folders.../filename
     * Eg.: test_id/folder1/test.txt
     *
     * @param object ObjectDTO accepted from frontend
     * @return
     */
    public static String buildUrl(ObjectDTO object, String uploadUrl) {
        ObjectMeta meta = object.getMeta();
        SaveConfiguration config = object.getConfig();

        // Bool 1: Check naming rules
        // Bool 2: mistaken uploadUrl: object.url() mismatch uploadUrl
        // Bool 3: mistaken filename: object.url() -> filename mismatch object.getMeta().getFilename()
        if (!isValidCustomizedPathUrl(object.getUrl(), meta.getUserId()) ||
                !object.getUrl().equals(uploadUrl) ||
                !Paths.get(object.getUrl()).getFileName().equals(object.getMeta().getFilename())) {

            object.setUrl(null);
        }

        if (object.getUrl() == null) {
            object.setUrl(meta.getUserId() + URL_SEPARATOR + meta.getFilename());
        }

        meta.setLocation(buildLocation(config, object.getUrl()));
        System.out.println("Debug: location: " + meta.getLocation());
        return object.getUrl();
    }

    /**
     * @deprecated moved to URLValidator
     * Image upload url only contains 2 parts: userId + image filename
     * When Us
     *
     * @param imageDTO
     * @param uploadUrl
     * @return
     */
    public static String buildImageUrl(ImageDTO imageDTO, String uploadUrl) {
        ObjectMeta meta = imageDTO.getMeta();
        SaveConfiguration config = imageDTO.getConfig();

        // Bool 1: Check naming rules
        // Bool 2: mistaken uploadUrl: object.url() mismatch uploadUrl
        // Bool 3: mistaken filename: object.url() -> filename mismatch object.getMeta().getFilename()
        Path p = Paths.get(uploadUrl);
        if (p.getNameCount() != 2 ||
                !p.getFileName().equals(imageDTO.getMeta().getFilename())) {
            imageDTO.setUrl(null);
        }

        if (imageDTO.getUrl() == null) {
            imageDTO.setUrl(meta.getUserId() + URL_SEPARATOR + meta.getFilename());
        }

        meta.setLocation(buildLocation(config, imageDTO.getUrl()));
        System.out.println("Debug: location: " + meta.getLocation());
        return imageDTO.getUrl();
    }


    public static String buildUrlForImage(ObjectDTO object, String uploadUrl) {
        ObjectMeta meta = object.getMeta();
        SaveConfiguration config = object.getConfig();

        // Bool 1: Check naming rules
        // Bool 2: mistaken uploadUrl: object.url() mismatch uploadUrl
        // Bool 3: mistaken filename: object.url() -> filename mismatch object.getMeta().getFilename()
        if (!isValidCustomizedPathUrl(object.getUrl(), meta.getUserId()) ||
                !object.getUrl().equals(uploadUrl) ||
                !Paths.get(object.getUrl()).getFileName().equals(object.getMeta().getFilename())) {

            object.setUrl(null);
        }

        if (object.getUrl() == null) {
            object.setUrl("image+/" + meta.getUserId() + URL_SEPARATOR + meta.getFilename());
        }

        meta.setLocation(buildLocation(config, object.getUrl()));
        System.out.println("Debug: location: " + meta.getLocation());
        return object.getUrl();
    }

    public static boolean isValidCustomizedPathUrl(String s, String userId) {
        if (s == null) return true;
        Path path;
        try {
            path = Paths.get(s);
        } catch (Exception e) {
            return false;
        }


        // Urls must start with "userId"
        if (!s.startsWith(userId)) return false;

        // Length must be no greater than 5
        if (path.getNameCount() > 5) return false;

        // If the 1st char is separator, it's dangerous to have access to the root folder.
        // Case like "\\" in the head will also cause exception when use File to create a directory.
        if (path.toString().charAt(0) == File.separatorChar) return false;

        if (!dictionaryFilter(s)) return false;

        return true;
    }

    private static String buildLocation(SaveConfiguration configuration, String url) {

        if (configuration.getDrive().equalsIgnoreCase("default")) {
            return DEFAULT_DRIVE + ";" + url;
        } else if (configuration.getDrive().equalsIgnoreCase("local")) {
            return "local;" + url;
        } else if (configuration.getDrive().equalsIgnoreCase("cloud")) {
            return "cloud;" + configuration.getToken() + ";" + url;
        } else throw new RuntimeException("URLCheck: Not a valid method");

    }

    // Just a simple simulation, not encrypted at all
    public static String encrypt(String s) {

        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String decrypt(String s) {

        return new String(Base64.getDecoder().decode(s.getBytes()));
    }

    /**
     * For
     *
     * @return
     */
    public static String readUrlFromRequestPath(String pathString) {
        // Remove the start "/"
        return pathString.replaceFirst("^/", "");
    }

    private static boolean dictionaryFilter(String s) {

        HashSet<String> filter = new HashSet<>();
        filter.add("\\"); // NTFS 分隔符
        filter.add(";"); // 我会用到
        filter.add(":");
        filter.add("*");
        filter.add("?");
        filter.add("\"");
        filter.add("\'");
        filter.add("..");
        filter.add("/./");
        filter.add("<");
        filter.add(">");
        filter.add("|");

        for (String f : filter) {
            if (s.contains(f)) return false;
        }

        return true;
    }


}
