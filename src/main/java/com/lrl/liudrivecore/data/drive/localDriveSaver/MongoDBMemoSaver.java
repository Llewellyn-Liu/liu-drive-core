package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.data.pojo.MemoBlock;
import com.lrl.liudrivecore.data.repo.MemoRepository;
import com.lrl.liudrivecore.service.tool.intf.MemoSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class MongoDBMemoSaver implements MemoSaver {

    private static Logger logger = LoggerFactory.getLogger(MongoDBMemoSaver.class);

    private MemoRepository repository;

    public MongoDBMemoSaver(MemoRepository repository) {

        this.repository = repository;

        logger.info("MongoDBMemoSaver initialized");
    }


    @Override
    public boolean save(String filename, byte[] data) {

        MemoBlock memo = new MemoBlock();
        memo.setTitle(filename);
        memo.setContent(Base64.getEncoder().encodeToString(data));
        memo.setUserId("save-undefined");


        return repository.save(memo) != null;
    }


    @Override
    public boolean save(MemoBlock memo) {
        return repository.save(memo) != null;
    }
}
