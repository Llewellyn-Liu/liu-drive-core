package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.MemoBlock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends MongoRepository<MemoBlock, String> {

    MemoBlock findByTitle(String title);

    List<MemoBlock> findAllByUserId(String userId);
}
