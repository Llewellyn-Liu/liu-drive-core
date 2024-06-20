package com.lrl.liudrivecore.service.stream.ws.config;

import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.schema.Schema;
import com.lrl.liudrivecore.data.repo.*;
import com.lrl.liudrivecore.service.ObjectService;
import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketTransportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketToolConfig {


    private FileDescriptionRepository repository;

    private LocalDriveSystemObjectSaver saver;

    Schema schema;
    ObjectService service;

    @Autowired
    protected WebSocketToolConfig(FileDescriptionRepository repository, LocalDriveSystemObjectSaver saver,
                                  Schema schema, ObjectService service) {
        this.repository = repository;
        this.saver = saver;
        this.schema = schema;
        this.service = service;
    }

    @Bean
    public WebSocketTransportHandler getWebSocketTransportHandler(){
        return new WebSocketTransportHandler(repository, saver, schema, service);
    }

}
