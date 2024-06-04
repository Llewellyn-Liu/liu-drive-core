package com.lrl.liudrivecore.service.security;

import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.service.util.mune.TokenSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class TokenManager {

    private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private SecretKeySpec key;

    @Autowired
    public TokenManager(@Value("${drive.sign-key}")String signKeyString){

        System.out.println("Debug: key ->"+signKeyString);
        byte[] keyArray = Base64.getDecoder().decode(signKeyString);
        key = new SecretKeySpec(keyArray, "AES");
    }


    public String generateToken(User user, TokenSpan tokenSpan){

        return updateToken(user.getUserId(), tokenSpan, null);

    }

    /**
     * Token format: "userId" + ";" + baseTime + ";" + expireTime
     * @param userId
     * @param tokenSpan
     * @param token
     * @return
     */
    public String updateToken(String userId, TokenSpan tokenSpan, String token) {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        if (token == null) {


            String base = userId + ";" + System.currentTimeMillis() + ";"
                    + (System.currentTimeMillis() + tokenSpan.showSpan());
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return (Base64.getEncoder().encodeToString(cipher.doFinal(base.getBytes())));
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                String origin = new String(cipher.doFinal(Base64.getDecoder().decode(token)));

                String[] parts = origin.split(";");

                if (parts[0].equals(userId) && validSpan(Long.parseLong(parts[2]))) {

                    if (tooMuchSpan(Long.parseLong(parts[1]), Long.parseLong(parts[2]), tokenSpan)) return token;
                    return updateToken(userId, tokenSpan, null);
                }
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }

        }

        return null;

    }


    private boolean validSpan(long parseLong) {
        
        return parseLong >= System.currentTimeMillis();
    }

    private boolean tooMuchSpan(long baseTime, long expireTime, TokenSpan tokenSpan) {

        return expireTime - baseTime >= 2 * tokenSpan.showSpan();

    }

    public void removeOwnerRecord(String userId) {
    }

    public boolean validToken(String userId, String token){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            String origin = new String(cipher.doFinal(Base64.getDecoder().decode(token)));

            String[] parts = origin.split(";");

            if(parts[0].equals(userId) && validSpan(Long.parseLong(parts[2]))){return true; }
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return false;

    }
}