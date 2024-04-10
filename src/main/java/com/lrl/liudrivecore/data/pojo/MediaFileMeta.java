package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MediaFileMeta extends StructuredFileMeta {

    protected String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "MediaFileMeta{" +
                "filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", dateCreated=" + dateCreated +
                ", url='" + url + '\'' +
                '}';
    }
}
