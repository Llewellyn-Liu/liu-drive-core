package com.lrl.liudrivecore.service.location;

import com.lrl.liudrivecore.data.pojo.StructuredFileMeta;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;

public class URLCheck {

    private final static String URL_SEPARATOR = "/";

    /**
     * Generate url which frontend can access the resource.
     * If user has provided an url as folder, set the filename after the folder.
     * Format: userId/folders.../filename
     * Eg.: test_id/folder1/test.txt
     * @param meta
     * @return
     */
    public static String buildUrl(StructuredFileMeta meta, SaveConfiguration configuration){
        if (!isValidCustomizedPathUrl(meta.getUrl(), meta.getUserId())) meta.setUrl(null);

        if (meta.getUrl() == null) {
            meta.setUrl(meta.getUserId() + URL_SEPARATOR + meta.getFilename());
        }

        meta.setLocation(buildLocation(configuration, meta.getUrl()));
        System.out.println("Debug: location: "+meta.getLocation());
        return meta.getUrl();
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
        if(!s.startsWith(userId)) return false;

        // Length must be no greater than 5
        if(path.getNameCount() > 5) return false;

        // If the 1st char is separator, it's dangerous to have access to the root folder.
        // Case like "\\" in the head will also cause exception when use File to create a directory.
        if (path.toString().charAt(0) == File.separatorChar) return false;

        if(!dictionaryFilter(s)) return false;

        return true;
    }

    private static String buildLocation(SaveConfiguration configuration, String url) {

        if(configuration.getMethod().equalsIgnoreCase("local")){
            return "local;"+url;
        } else if (configuration.getMethod().equalsIgnoreCase("cloud")) {
            return "cloud;"+ configuration.getToken()+";"+url;
        }
        else throw new RuntimeException("URLCheck: Not a valid method");

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
     * @return
     */
    public static String readUrlFromRequestPath(String pathString){
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

        for(String f: filter){
            if(s.contains(f)) return false;
        }

        return true;
    }




}
