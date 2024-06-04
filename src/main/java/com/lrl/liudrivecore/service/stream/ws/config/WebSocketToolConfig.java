package com.lrl.liudrivecore.service.stream.ws.config;

import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.repo.*;
import com.lrl.liudrivecore.service.dir.url.URLValidator;
import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketTransportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketToolConfig {


    private FileDescriptionRepository repository;

    private LocalDriveSystemObjectSaver saver;

    URLValidator validator;

    @Autowired
    protected WebSocketToolConfig(FileDescriptionRepository repository, LocalDriveSystemObjectSaver saver,
                                  URLValidator validator) {
        this.repository = repository;
        this.saver = saver;
        this.validator = validator;
    }

    @Bean
    public WebSocketTransportHandler getWebSocketTransportHandler(){
        return new WebSocketTransportHandler(repository, saver, validator);
    }

}
