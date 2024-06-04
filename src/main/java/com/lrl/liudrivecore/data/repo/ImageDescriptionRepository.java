package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageDescriptionRepository extends MongoRepository<ImageDescription, String> {

    ImageDescription getImageDescriptionByUrl(String url);

    List<ImageDescription> getAllByMetaUserId(String userId, Pageable pageable);

}
