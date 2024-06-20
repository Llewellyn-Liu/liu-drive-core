package com.lrl.liudrivecore.data.pojo.mongo;

/**
 * Nested record for
 */
public class FileDescriptionSimpleRecord {
    String url;

    String type;

    int accessibility;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileDescriptionSimpleRecord{" +
                "url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        return  ((FileDescriptionSimpleRecord) obj).getUrl().equals(this.getUrl());
    }
}
