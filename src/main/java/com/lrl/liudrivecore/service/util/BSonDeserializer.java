package com.lrl.liudrivecore.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.Binary;

import java.util.Base64;
import java.util.Objects;

/**
 * This class provides static method to handle bson values.
 * Basic usage:
 *     When a json is saved as bson in binary format, this class will generate values accordingly
 */
public class BSonDeserializer {

    public static String getJsonString(Binary b){

        System.out.println(": "+new String(b.getData()));
        String s = new String(b.getData());
        return s;
    }

    public static String getJsonBase64(Binary b){
        return new String(Base64.getEncoder().encodeToString(getJsonString(b).getBytes()));
    }


    public static byte[] getJsonByte(Binary b){
        return getJsonString(b).getBytes();
    }



}
