package com.lrl.liudrivecore.service.tool.runtime;

import com.lrl.liudrivecore.service.tool.mune.TokenSpan;
import com.lrl.liudrivecore.service.tool.template.TokenTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TokenManager {

    private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private PriorityQueue<TokenTemplate> tokenElementHeap = new PriorityQueue<>();

    private HashMap<String, String> userLatestToken = new HashMap<>();

    private int count = 0;


    public TokenManager() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                housekeeping();
            }
        }, 1000, 60 * 1000L);
    }

    public String generateToken(String userId, TokenSpan tokenSpan, long currentTimeMillis) {

        long timestamp = currentTimeMillis + tokenSpan.showSpan();
        String pattern = userId + ";" + timestamp;
        String salt = System.getenv("LIU-salt");
        TextEncryptor encryptor = Encryptors.text(System.getenv("LIU-encryptString"), salt);
        String token = encryptor.encrypt(pattern);
        return token;
    }

    public TokenTemplate generateTokenElement(String userId, TokenSpan tokenSpan) {
        long now = System.currentTimeMillis();
        return new TokenTemplate(userId, now + tokenSpan.showSpan(), generateToken(userId, tokenSpan, now));
    }

    /**
     * Push a stuffed tokenElement into this Manager
     *
     * @param tokenTemplateEntry userId, token, timestamp combines and serves as a unit to be managed
     */
    public void push(TokenTemplate tokenTemplateEntry) {

        logger.info("Debug: tokenElement:" + tokenTemplateEntry.toString());
        String userId = tokenTemplateEntry.getUserId();

        if (userLatestToken.containsKey(userId)) {
            update(tokenTemplateEntry);
        } else {
            tokenElementHeap.add(tokenTemplateEntry);

            userLatestToken.put(userId, tokenTemplateEntry.getToken());

            logger.info("Debug: tokenManager: heap: " + tokenElementHeap.peek().toString());

            count++;

            logger.info("Debug: tokenManager: count: " + count);
        }


    }


    public boolean update(TokenTemplate tokenTemplate) {

        String userId = tokenTemplate.getUserId();

        //Protection code 1 - UserId must exist in HashMap
        if (!userLatestToken.containsKey(userId)) return false;

        String oldToken = userLatestToken.get(userId);
        TokenTemplate oldTokenElement = new TokenTemplate(userId, getTimestamp(oldToken), oldToken);

        if (tokenElementHeap.remove(oldTokenElement)) {
            logger.info("Refresh Token: Old token removed from heap");
            push(tokenTemplate);
        } else {
            //Protection code 2 - Timestamp must be within valid span
            logger.info("Refresh Token: Failed to remove old token from heap");
            return false;
        }

        userLatestToken.put(userId, tokenTemplate.getToken());
        return true;

    }

    private void housekeeping() {

        logger.info("HouseKeeping... " + System.currentTimeMillis());
        if (count == 0) {
            logger.info("HouseKeeping: Empty");
            return;
        }

        while (count > 0 && tokenElementHeap.peek().getTimestamp() < System.currentTimeMillis()) {
            testHouseKeeping();

            TokenTemplate currentTokenElement = tokenElementHeap.remove();
            userLatestToken.remove(currentTokenElement.getUserId());
            logger.info("HouseKeeping: remove, tokenTime:" + currentTokenElement.getTimestamp() + ", current: " + System.currentTimeMillis());
            count--;
        }
    }

    private void testHouseKeeping() {
        for (TokenTemplate tt : this.tokenElementHeap
        ) {
            System.out.println(tt.getUserId() + ", " + tt.getTimestamp());
        }
    }

    public boolean validToken(String token) {
        String[] tokenDecodedElements = Encryptors.text(System.getenv("LIU-encryptString"), System.getenv("LIU-salt"))
                .decrypt(token).split(";");

        return userLatestToken.get(tokenDecodedElements[0]).equals(token);
    }

    public boolean validTimestamp(String timestamp) {
        long timestampTimeMillis = Long.parseLong(timestamp);
        long currentTimeMillis = System.currentTimeMillis();
        if (timestampTimeMillis < currentTimeMillis) {
            logger.info("Current time: " + System.currentTimeMillis() + " expires.");
            return false;
        }

        logger.info("Debug: Current time: " + System.currentTimeMillis() + ", timestamp: " + timestamp);
        return true;
    }


    public String getUserId(String token) {
        String tokenDecoded = Encryptors.text(System.getenv("LIU-encryptString"), System.getenv("LIU-salt"))
                .decrypt(token);

        logger.info("Debug: tokenDecode:" + tokenDecoded);
        return tokenDecoded.split(";")[0];
    }


    private long getTimestamp(String token) {
        String tokenDecoded = Encryptors.text(System.getenv("LIU-encryptString"), System.getenv("LIU-salt"))
                .decrypt(token);
        return Long.parseLong(tokenDecoded.split(";")[1]);
    }

    /**
     * Remove user's trail
     * @param userId
     */
    public void removeOwnerRecord(String userId){
        removeOldEntry(userId);
    }

    private void addNewEntry(String userId) {
        TokenTemplate newTemplate = generateTokenElement(userId, TokenSpan.SHORT_SPAN);
        tokenElementHeap.add(newTemplate);
        userLatestToken.put(userId, newTemplate.getToken());
    }

    private void removeOldEntry(String userId) {
        if(!userLatestToken.containsKey(userId)) return;

        String token = userLatestToken.remove(userId);

        TokenTemplate mockOldTemplate = new TokenTemplate(userId, getTimestamp(token), token);
        boolean isDeletedFromHeap = tokenElementHeap.remove(mockOldTemplate);
        if (isDeletedFromHeap) logger.info("AlterTokenOwner: Old entry removed from heap");
        else logger.info("AlterTokenOwner: Remove failed");

        //HashTable part
        userLatestToken.remove(userId);
    }
}