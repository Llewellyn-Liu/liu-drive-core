package com.lrl.liudrivecore.data.pojo.mongo;

import java.util.List;

public class ObjectMetaWithTag extends ObjectMeta{

    protected List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ObjectMetaWithTag{" +
                "tags=" + tags +
                ", filename='" + filename + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", author='" + author + '\'' +
                ", dateCreated=" + dateCreated +
                ", lastModified=" + lastModified +
                ", etag='" + etag + '\'' +
                ", userId='" + userId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
