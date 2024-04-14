package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectFileMetaRepository extends JpaRepository<ObjectFileMeta, Long> {

    ObjectFileMeta getByFilename(String filename);

    ObjectFileMeta getByUrl(String url);

    List<ObjectFileMeta> findAllByUserId(String userId, Pageable pageable);

    List<ObjectFileMeta> findAllByUserIdAndType(String userId, String type);

}
