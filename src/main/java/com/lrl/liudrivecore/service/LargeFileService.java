package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.service.tool.template.LargeFileChunk;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LargeFileService {

    Hashtable<String, LargeFileChunk[]> largeFileRegistry;

    Hashtable<String, String[]> superFileRegistry;

    public LargeFileService(){
        largeFileRegistry = new Hashtable<>();
        superFileRegistry = new Hashtable<>();
    }

    public boolean hasUserId(String userId, boolean isSuper){

        return isSuper ? superFileRegistry.containsKey(userId) : largeFileRegistry.containsKey(userId);
    }

    public List<Integer> test(String userId, boolean isSuper){
        return isSuper ? testLargeFile(userId) : testSuperFile(userId);
    }

    private List<Integer> testLargeFile(String userId) {
        ArrayList<Integer> result;

        if(!largeFileRegistry.containsKey(userId)) {
            result = new ArrayList<>();
            result.add(-1);
            return result;
        }

        LargeFileChunk[] fileChunks = largeFileRegistry.get(userId);
        for(LargeFileChunk chunk: fileChunks){

        }

        return null;
    }

    private List<Integer> testSuperFile(String userId) {
        return null;
    }



}
