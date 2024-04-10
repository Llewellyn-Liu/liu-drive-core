package com.lrl.liudrivecore.service.tool.stereotype;

import com.lrl.liudrivecore.data.pojo.StructuredFileMeta;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathStereotype {

    // This should be unified for frontend usages.
    private final static String PATH_SEPARATOR = "/";


    /**
     * Generate url which frontend can access the resource.
     * If user has provided an url as folder, set the filename after the folder.
     * Format: userId/folders.../filename
     * Eg.: test_id/folder1/test.txt
     * @param meta
     * @return
     */
    public static String buildUrl(StructuredFileMeta meta){
        if (meta.getUrl() != null && !isValidCustomizedPathUrl(meta.getUrl())) meta.setUrl(null);

        if (meta.getUrl() == null) {
            meta.setUrl(meta.getUserId() + PATH_SEPARATOR + meta.getFilename());
            return meta.getFilename();
        } else {
            String s = Paths.get(meta.getUserId(), meta.getUrl(), meta.getFilename()).toString();
            meta.setUrl(s);
            return s;
        }
    }

    public static boolean isValidCustomizedPathUrl(String s) {
        if (s == null) return true;
        Path path;
        try {
            path = Paths.get(s);

            Path lengthTestPath = Paths.get(s);
            int count = 0;
            while(lengthTestPath!=null){
                lengthTestPath = lengthTestPath.getParent();
                count++;
            }
            if(count > 5) return false;
        } catch (Exception e) {
            return false;
        }

        // If the 1st char is separator, it's dangerous to have access to the root folder.
        // Case like "\\" in the head will also cause exception when use File to create a directory.
        if (path.toString().charAt(0) == File.separatorChar) return false;

        // Even though it is ok to have dots within a dir name, I think it is confusing.
        if (path.getFileName().toString().contains(".")) return false;


        return true;
    }

    // Not accomplished: need add userId check for "baseFolderName" down below.
//    public static boolean isValidPathUrlToSave(String s) {
//        if (s == null) return false;
//
//        Path path;
//        try {
//            path = Paths.get(s);
//
//            Path testPath = Paths.get(s);
//            int count = 1;
//            while(testPath.getParent()!=null){
//                testPath = testPath.getParent();
//                count++;
//            }
//            if(count > 5) return false;
//            String baseFolderName = testPath.toString();
//        } catch (Exception e) {
//            return false;
//        }
//
//        // If the 1st char is separator, it's dangerous to have access to the root folder.
//        // Case like "\\" in the head will also cause exception when use File to create a directory.
//        if (path.toString().charAt(0) == File.separatorChar) return false;
//
//        // Even though it is ok to have dots within a dir name, I think it is confusing.
//        if (path.getFileName().toString().contains(".")) return false;
//
//
//        return true;
//    }
}
