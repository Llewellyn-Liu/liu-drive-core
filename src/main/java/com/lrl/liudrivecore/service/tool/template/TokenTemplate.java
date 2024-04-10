package com.lrl.liudrivecore.service.tool.template;

public class TokenTemplate implements Comparable<TokenTemplate>{

    private String userId;

    private long timestamp;

    private String token;

    public TokenTemplate(String userId, long timestamp, String token) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenTemplate{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public int compareTo(TokenTemplate o) {
        if(this.timestamp > o.getTimestamp()) return 1;
        else if (this.timestamp ==  o.timestamp && this.userId.equals(o.userId) ) {
            return 0;
        }
        else return -1;
    }
}
