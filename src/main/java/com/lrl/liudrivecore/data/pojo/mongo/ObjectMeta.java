package com.lrl.liudrivecore.data.pojo.mongo;

import java.util.Objects;

public class ObjectMeta extends AbstractMeta<String>{


    @Override
    public String toString() {
        return "ObjectMeta{" +
                "filename='" + filename + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", author='" + author + '\'' +
                ", dateCreated=" + dateCreated +
                ", lastModified=" + lastModified +
                ", etag='" + etag + '\'' +
                ", userId='" + userId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || obj.getClass() != ObjectMeta.class){
            return false;
        }else{
            ObjectMeta o = (ObjectMeta) obj;

            // Modified by ChatGPT: Objects.equals() will handle null values
            return Objects.equals(etag, o.etag) &&
                    userId == o.userId &&
                    Objects.equals(filename, o.filename) &&
                    Objects.equals(mimeType, o.mimeType) &&
                    Objects.equals(dateCreated, o.dateCreated) &&
                    Objects.equals(lastModified, o.lastModified);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEtag(), this.getUserId(), this.getFilename(), this.getMimeType(), this.getDateCreated(), this.getLastModified());
    }
}
