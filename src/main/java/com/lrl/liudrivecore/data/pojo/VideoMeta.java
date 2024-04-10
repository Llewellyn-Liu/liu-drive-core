package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "DR_VIDEOMETA")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class VideoMeta extends MediaFileMeta {

    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "VideoMeta{" +
                "filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", dateCreated=" + dateCreated +
                ", author='" + author + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}