package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.pojo.VideoMeta;
import com.lrl.liudrivecore.data.repo.VideoMetaRepository;
import com.lrl.liudrivecore.service.location.DefaultSaveConfiguration;
import com.lrl.liudrivecore.service.location.URLCheck;
import com.lrl.liudrivecore.service.tool.intf.VideoReader;
import com.lrl.liudrivecore.service.tool.intf.VideoSaver;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class VideoService {

    private VideoMetaRepository repository;

    private VideoSaver saver;

    private VideoReader reader;

    private static Logger logger = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    public VideoService(VideoMetaRepository repository,
                        VideoReader reader,
                        VideoSaver saver) {
        this.repository = repository;
        this.saver = saver;
        this.reader = reader;

    }


    @Transactional
    public boolean upload(VideoMeta meta, byte[] data, DefaultSaveConfiguration configuration) {

        logger.info("VideoService upload: " + meta);

        // load additional attrs
        URLCheck.buildUrl(meta, configuration);
        meta.setDateCreated(ZonedDateTime.now());

        // Save Data
        saveFileData(meta.getUrl(), data);

        meta.setLocation(URLCheck.encrypt(meta.getUrl()));

        // Save Meta
        saveVideoMeta(meta);



        return true;
    }

    private void saveFileData(String pathUrl, byte[] data) {

        saver.save(pathUrl, data);

    }

    private void saveVideoMeta(VideoMeta meta) {
        if (repository.getByUrl(meta.getUrl()) != null) {
            throw new RuntimeException("Video url conflicts with existing resource");
        }
        repository.save(meta);
    }
}
