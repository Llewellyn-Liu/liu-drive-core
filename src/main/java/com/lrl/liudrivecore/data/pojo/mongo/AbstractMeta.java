package com.lrl.liudrivecore.data.pojo.mongo;

import java.time.ZonedDateTime;

/**
 *
 * @param <T> for timestamps class
 */
public class AbstractMeta<T> {

    protected String filename;

    protected String mimeType;

    protected String author;

    protected T dateCreated;

    protected T lastModified;

    protected String location;
    protected String etag;

    protected String userId;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public T getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(T dateCreated) {
        this.dateCreated = dateCreated;
    }

    public T getLastModified() {
        return lastModified;
    }

    public void setLastModified(T lastModified) {
        this.lastModified = lastModified;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
