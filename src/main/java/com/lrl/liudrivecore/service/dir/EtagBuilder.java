package com.lrl.liudrivecore.service.dir;

import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EtagBuilder {

    public static String build(FileDescription fd){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.digest(fd.getId().getBytes());
            md.digest(fd.toString().getBytes());

            String etag = Base64.getEncoder().encodeToString(md.digest());
            fd.getMeta().setEtag(etag);
            return etag;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
