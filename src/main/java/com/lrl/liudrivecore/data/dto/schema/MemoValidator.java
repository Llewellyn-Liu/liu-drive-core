package com.lrl.liudrivecore.data.dto.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.pojo.mongo.Memo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

public class MemoValidator implements SchemaValidator<Memo> {

    Logger logger = LoggerFactory.getLogger(MemoValidator.class);

    private ObjectMapper mapper;

    public MemoValidator() {
        mapper = new ObjectMapper();
    }

    @Override
    public boolean isValid(Memo memo) {

        // UserId must exist
        // Url is not checked
        if (memo.getUserId() == null || memo.getUserId() == "")
            return false;

        // Check if is good json
        ObjectMapper mapper = new ObjectMapper();

        boolean isGoodJson = false;
        try {
            byte[] data = memo.getData().getData();
            // Blob size limit check
            if (data.length > 1 * 1000 * 1000) return false;

            JsonNode root = mapper.readTree(data);
            if (root != null) isGoodJson = true;
        } catch (Exception e) {
            logger.info(String.format("Memo send by %s is invalid for: %s", memo.getUserId(), "Bad Json format"));
        }
        if (!isGoodJson) return false;

        // Etag should be null
        if (memo.getEtag() != null) return false;

        // Check if length valid
        if (memo.getUrl() != null) {
            String[] urlElements = memo.getUrl().split("/");
            // url element size must <= 5 and >= 2
            if (urlElements.length > 5 || urlElements.length < 2) return false;
            if (!urlElements[0].equals(memo.getUserId())) return false;
        }


        return true;
    }

    public void filter(Memo memo) {

        if (memo.getEtag() != null) memo.setEtag(null);

    }

    /**
     * When post a Memo, the path should be userId. If isEnforced, path could be true.
     * When PUT and PATCH, the path is needed.
     *
     * @param memo
     * @param path
     * @param isEnforced
     * @return
     */
    public Memo isProtocolValid(Memo memo, String path, boolean isEnforced, HttpMethod method) {

        if (method != HttpMethod.POST && method != HttpMethod.PUT && method != HttpMethod.PATCH) return null;

        if (path == null || path.equals("")) return null;

        filter(memo);

        if (!isValid(memo)) return null;

        if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            String[] pathElements = path.split("/");
            // url element size must <= 5 and >= 2
            if (pathElements.length > 5 || pathElements.length < 2) return null;
            if (!pathElements[0].equals(memo.getUserId())) return null;

            if (memo.getUrl() == null && isEnforced) memo.setUrl(path);
            if (memo.getUrl() == null || !memo.getUrl().equals(path)) return null;
        }
        // When POST and !isEnforced, url should not be null
        else if (memo.getUrl() == null && !isEnforced) return null;

        return memo;

    }
}
