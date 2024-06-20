package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * @deprecated
 * Moved to ObjectDescription using MongoDB
 */
@Entity
@Table(name = "DR_OBJECTFILEMETA")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ObjectFileMeta extends StructuredFileMeta {

    @Override
    public String toString() {
        return "ObjectFileMeta{" +
                "filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", dateCreated=" + dateCreated +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}