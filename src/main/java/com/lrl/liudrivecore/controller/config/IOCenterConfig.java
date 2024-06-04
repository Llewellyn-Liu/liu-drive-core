package com.lrl.liudrivecore.controller.config;

import com.lrl.liudrivecore.data.drive.localDriveReader.*;
import com.lrl.liudrivecore.data.drive.localDriveSaver.*;
import com.lrl.liudrivecore.data.repo.MemoRepository;
import com.lrl.liudrivecore.service.util.intf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Settings of data source destination.
 *
 */
@Configuration
public class IOCenterConfig {

    @Autowired
    private MemoRepository repository;

    @Value("${drive.root:drive}")
    private String defaultRoot;

    @Value("${drive.image.root:drive}")
    private String defaultImageRoot;

    @Bean
    public LocalDriveSystemObjectSaver getLocalDriveSystemObjectSaver(){
        return new LocalDriveSystemObjectSaver(defaultRoot);
    }

    @Bean
    public LocalDriveSystemObjectReader getLocalDriveSystemObjectReader(){
        return new LocalDriveSystemObjectReader(defaultRoot);
    }

    @Bean
    public LocalDriveSystemImageSaver getLocalDriveSystemImageSaver(){
        return new LocalDriveSystemImageSaver(defaultImageRoot);
    }
    @Bean
    public LocalDriveSystemImageReader getLocalDriveSystemImageReader(){
        return new LocalDriveSystemImageReader(defaultImageRoot);
    }

}
