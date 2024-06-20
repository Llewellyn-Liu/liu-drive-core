package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.mongo.Memo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MemoDescriptionRepository extends MongoRepository<Memo, String> {

    Memo findByUrl(String url);

    List<Memo> findAllByUserId(String userId);
}
