package com.lrl.liudrivecore.controller.config;

import com.lrl.liudrivecore.data.drive.localDriveReader.*;
import com.lrl.liudrivecore.data.drive.localDriveSaver.*;
import com.lrl.liudrivecore.data.repo.MemoRepository;
import com.lrl.liudrivecore.service.tool.intf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IOCenterConfig {

    @Autowired
    private MemoRepository repository;

    @Value("${drive.root:drive")
    private String defaultRoot;

    @Bean
    public LocalDriveSystemObjectSaver getLocalDriveSystemObjectSaver(){
        return new LocalDriveSystemObjectSaver(defaultRoot);
    }

    @Bean
    public LocalDriveSystemObjectReader getLocalDriveSystemObjectReader(){
        return new LocalDriveSystemObjectReader(defaultRoot);
    }

    @Bean
    public ImageSaver getImageSaver(){
        return new LocalDriveSystemImageSaver();
    }
    @Bean
    public ImageReader getImageReader(){
        return new LocalDriveSystemImageReader();
    }

    @Bean
    public MemoReader getMemoReader(){
        return new MongoDBMemoReader(repository);
    }

    @Bean
    public MemoSaver getMemoSaver(){
        return new MongoDBMemoSaver(repository);
    }

    @Bean
    public VideoSaver getVideoSaver(){
        return new LocalDriveSystemVideoSaver();
    }
    @Bean
    public VideoReader getVideoReader(){
        return new LocalDriveSystemVideoReader();
    }

    @Bean
    public AudioSaver getAudioSaver(){
        return new LocalDriveSystemAudioSaver();
    }
    @Bean
    public AudioReader getAudioReader(){
        return new LocalDriveSystemAudioReader();
    }

}
