package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.AudioMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioMetaRepository extends JpaRepository<AudioMeta, Long> {

    AudioMeta getByFilename(String filename);

    List<AudioMeta> findAllByUserId(String userId, Pageable pageable);

    AudioMeta getByUrl(String url);
}
