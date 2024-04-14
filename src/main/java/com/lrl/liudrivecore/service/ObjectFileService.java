package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.pojo.MemoBlock;
import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import com.lrl.liudrivecore.data.repo.ObjectFileMetaRepository;
import com.lrl.liudrivecore.service.location.SaveConfiguration;
import com.lrl.liudrivecore.service.location.URLCheck;
import com.lrl.liudrivecore.service.tool.intf.MemoReader;
import com.lrl.liudrivecore.service.tool.intf.MemoSaver;
import com.lrl.liudrivecore.service.tool.intf.ObjectFileReader;
import com.lrl.liudrivecore.service.tool.intf.ObjectFileSaver;
import com.lrl.liudrivecore.service.tool.template.ObjectFile;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ObjectFileService {

    private static Logger logger = LoggerFactory.getLogger(ObjectFileService.class);

    private ObjectFileMetaRepository repository;

    private LocalDriveSystemObjectSaver localDriveSystemObjectSaver;

    private ObjectFileReader reader;

    private MemoSaver memoSaver;

    private MemoReader memoReader;

    private static final int PAGE_SIZE = 20;


    @Autowired
    public ObjectFileService(ObjectFileMetaRepository repository,
                             LocalDriveSystemObjectSaver localDriveSystemObjectSaver,
                             ObjectFileReader reader,
                             MemoSaver memoSaver,
                             MemoReader memoReader) {
        this.repository = repository;
        this.localDriveSystemObjectSaver = localDriveSystemObjectSaver;
        this.reader = reader;
        this.memoReader = memoReader;
        this.memoSaver = memoSaver;
    }


    @Transactional
    public boolean upload(ObjectFileMeta meta, byte[] data, SaveConfiguration configuration) throws RuntimeException{
        logger.info("ObjectFileService upload(meta): " + meta.toString());

        URLCheck.buildUrl(meta, configuration);

        if (!saveObjectFileMeta(meta)) {
            logger.error("ObjectFileMeta failed to save.");
            throw new RuntimeException("Filename already in database");
        } else logger.info("File saved in database");

        if (saveObjectFileData(meta.getLocation(), data)) {
            logger.info("File saved in file system.");
            return true;
        } else{
            logger.error("ObjectFile data failed to be saved.");
            throw new RuntimeException("Filename already exists on drive");
        }
    }



    private boolean saveObjectFileMeta(ObjectFileMeta meta) {
        if (repository.getByFilename(meta.getFilename()) != null) {
            logger.info("Filename exists in database.");
            return false;
        }

        try {
            meta.setDateCreated(ZonedDateTime.now());
            repository.save(meta);
        } catch (Exception e) {
            return false;
        }

        return true;

    }

    //Keep the type, objectFile handles different types.
    private boolean saveObjectFileData(String location, byte[] data) {

        if(location.startsWith("local")){
            return localDriveSystemObjectSaver.save(location, data);
        } else if (location.startsWith("cloud")) {

        }else { }

        return false;

    }

    public boolean uploadMemo(MemoBlock memo){

        return memoSaver.save(memo);
    }


    /**
     * Direct thought. Security check is not included.
     * @param url
     */
    public ObjectFile get(String url) {

        ObjectFileMeta meta = repository.getByUrl(url);

        byte[] data = reader.readAll(meta.getLocation());

        return ObjectFile.copy(meta, data);
    }

    /**
     * Retrieve list of filemeta using userId
     *
     * @param userId
     * @param page
     * @return
     */
    public List<ObjectFileMeta> getList(String userId, Integer page) {
        List<ObjectFileMeta> list = repository.findAllByUserId(userId, PageRequest.of(page, PAGE_SIZE));

        // Clear sensitive data
        for(ObjectFileMeta m: list){
            m.setLocation(null);
        }
        return list;
    }


    /**
     * Retrieve list of memo using userId
     *
     * @param userId
     * @return
     */
    public List<MemoBlock> getMemoList(String userId) {
        List<MemoBlock> list = memoReader.getListByUserId(userId);
        return list;
    }



}
