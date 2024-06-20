package com.lrl.liudrivecore.service.dir;

import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import com.lrl.liudrivecore.data.pojo.mongo.Memo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EtagBuilder {

    public static String build(FileDescription fd){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(fd.getId().getBytes());
            md.update(fd.toString().getBytes());

            String etag = Base64.getEncoder().encodeToString(md.digest());
            fd.getMeta().setEtag(etag);
            return etag;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static String build(ImageDescription imd){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(imd.getId().getBytes());
            md.update(imd.toString().getBytes());

            String etag = Base64.getEncoder().encodeToString(md.digest());
            imd.getMeta().setEtag(etag);
            return etag;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static String build(Memo memo){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(memo.getData().getData());
            md.update(memo.getUrl().getBytes());
            md.update(memo.getUserId().getBytes());
            md.update((""+System.currentTimeMillis()).getBytes());
            String etag = Base64.getEncoder().encodeToString(md.digest());
            memo.setEtag(etag);
            return etag;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
