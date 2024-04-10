package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.VideoMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoMetaRepository extends JpaRepository<VideoMeta, Long> {

    VideoMeta getByFilename(String filename);

    List<VideoMeta> findAllByUserId(String userId, Pageable pageable);

    VideoMeta getByUrl(String url);

    VideoMeta deleteByUrl(String url);

}
