package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.CustomizedGalleryData;
import com.lrl.liudrivecore.data.pojo.ImageMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @deprecated
 * Replaced by ImageDescriptionRepository in key-value format
 * This Class is kept for future reference.
 */
public interface ImageMetaRepository extends JpaRepository<ImageMeta, Long> {

    ImageMeta getByFilename(String filename);

    List<ImageMeta> findAllByUserId(String userId, Pageable pageable);

    ImageMeta getByUrl(String url);

    @Query(value = "SELECT filename, type, url, user_id as userId, accessibility, date_created as dateCreated, tags, author, scale from dr_imagemeta where user_id = ?1 " +
            "UNION ALL select filename, type, url, user_id as userId, accessibility, date_created as dateCreated, tags, author, 1 as scale from dr_videometa where user_id = ?1 ;"
            , nativeQuery = true)
    List<CustomizedGalleryData> getJointQueryFromImageAndVideo(String userId, Pageable pageable);

}
