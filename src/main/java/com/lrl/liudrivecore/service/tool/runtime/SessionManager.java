package com.lrl.liudrivecore.service.tool.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
public class SessionManager {

    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private HashMap<String, Node> sessionMap = new HashMap<>();


    public void registerSession(String userId, String sessionId){

        if(!sessionMap.containsKey(userId)){
            sessionMap.put(userId, new Node(sessionId));
        }
        else {
            Node currentNode = sessionMap.get(userId);
            while(currentNode.next != null) currentNode = currentNode.next;
            currentNode.next = new Node(sessionId);
        }

        logger.info("SessionManager: added session:"+ userId+" -> "+sessionId);
    }

    public boolean contains(String userId, String sessionId){
        if(!sessionMap.containsKey(userId)) {
            logger.info("Debug: userId not found, userId: "+userId);
            return false;
        }

        Node currentNode = sessionMap.get(userId);
        while(currentNode!=null){
            if(currentNode.sessionId == sessionId) return true;
            currentNode = currentNode.next;
        }
        logger.info("Debug: session not found");
        return false;

    }


    /**
     * The remove() method is completely dependent on the TokenManager. When user's token expires,
     * the record in SessionManager is also removed.
     * @param userId
     */
    public void remove(String userId){
        sessionMap.remove(userId);
    }

    private class Node{

        String sessionId;

        Node next;

        Node(){

        }

        Node(String sessionId){
            this.sessionId = sessionId;
        }

    }

}
