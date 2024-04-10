package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "DR_AUDIOMETA")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AudioMeta extends MediaFileMeta {

    @Override
    public String toString() {
        return "AudioMeta{" +
                "filename='" + filename + '\'' +
                ", dateCreated=" + dateCreated +
                ", author='" + author + '\'' +
                ", accessibility=" + accessibility +
                ", userId='" + userId + '\'' +
                ", tags='" + tags + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}