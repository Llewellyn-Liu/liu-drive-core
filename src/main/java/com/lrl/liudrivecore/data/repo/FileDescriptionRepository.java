package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FileDescriptionRepository extends MongoRepository<FileDescription, String> {

    FileDescription getFileDescriptionByUrl(String url);

    @Query("{'meta': {'userId': ?0}}")
    List<FileDescription> findObjectByUserId(String userId, Pageable pageable);


    List<FileDescription> findAllByMetaUserId(String userId);

    @Query("{'meta.mimeType': {$regex: '^video', $options: 'i'}, 'meta.userId': ?0}")
    List<FileDescription> findAllVideoTypeOfUserId(String userId, Pageable pageable);

    int countAllByMetaUserId(String userId);

}
