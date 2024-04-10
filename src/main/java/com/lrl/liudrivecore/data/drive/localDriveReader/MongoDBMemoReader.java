package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.data.pojo.MemoBlock;
import com.lrl.liudrivecore.data.repo.MemoRepository;
import com.lrl.liudrivecore.service.tool.intf.MemoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MongoDBMemoReader implements MemoReader {

    private static Logger logger = LoggerFactory.getLogger(MongoDBMemoReader.class);

    private MemoRepository repository;

    public MongoDBMemoReader(MemoRepository repository) {
        this.repository = repository;
        logger.info("MongoDBMemoReader initialized");
    }


    @Override
    public byte[] readAll(String title) {

        return repository.findByTitle(title).getContent().getBytes();

    }

    @Override
    public int bufferRead(String filename, byte[] buffer, Integer start, Integer end) {
        return 0;
    }

    @Override
    public List<MemoBlock> getListByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }
}
