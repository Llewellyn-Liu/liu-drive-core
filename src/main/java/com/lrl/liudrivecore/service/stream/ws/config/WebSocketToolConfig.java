package com.lrl.liudrivecore.service.stream.ws.config;

import com.lrl.liudrivecore.data.repo.AudioMetaRepository;
import com.lrl.liudrivecore.data.repo.ImageMetaRepository;
import com.lrl.liudrivecore.data.repo.ObjectFileMetaRepository;
import com.lrl.liudrivecore.data.repo.VideoMetaRepository;
import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketTransportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketToolConfig {

    private ObjectFileMetaRepository objectFileMetaRepository;

    private ImageMetaRepository imageMetaRepository;

    private VideoMetaRepository videoMetaRepository;

    private AudioMetaRepository audioMetaRepository;

    @Autowired
    protected WebSocketToolConfig(ObjectFileMetaRepository objectFileMetaRepository,
                                  ImageMetaRepository imageMetaRepository,
                                  VideoMetaRepository videoMetaRepository,
                                  AudioMetaRepository audioMetaRepository) {
        this.objectFileMetaRepository = objectFileMetaRepository;
        this.imageMetaRepository = imageMetaRepository;
        this.videoMetaRepository = videoMetaRepository;
        this.audioMetaRepository = audioMetaRepository;
    }

    @Bean
    public WebSocketTransportHandler getWebSocketTransportHandler(){
        return new WebSocketTransportHandler(objectFileMetaRepository, imageMetaRepository, videoMetaRepository, audioMetaRepository);
    }

}
