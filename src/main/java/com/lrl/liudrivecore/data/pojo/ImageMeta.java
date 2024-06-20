package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;


/**
 * @deprecated
 * Moved to ImageDescription using MongoDB
 */
@Entity
@Table(name = "DR_IMAGEMETA")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ImageMeta extends MediaFileMeta {

    private Integer scale;

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }


    @Override
    public String toString() {
        return "ImageMeta{" +
                "filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", dateCreated=" + dateCreated +
                ", url='" + url + '\'' +
                ", scale=" + scale +
                '}';
    }
}