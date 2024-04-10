package com.lrl.liudrivecore.data.pojo;

/**
 * Customize query from Spring Data JPA
 */
public interface CustomizedGalleryData {

    Integer getAccessibility();

    String getDateCreated();

    String getFilename();

    String getTags();

    String getType();

    String getUrl();

    String getUserId();

    String getAuthor();

    Integer getScale();
}
