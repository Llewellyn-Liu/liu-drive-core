package com.lrl.liudrivecore.service.stream.ws.config;

import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketBinaryHandler;
import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketMessageHandler;
import com.lrl.liudrivecore.service.stream.ws.handler.WebSocketTransportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketPathCombiner implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private WebSocketTransportHandler webSocketTransportHandler;

    @Autowired
    public WebSocketPathCombiner(WebSocketTransportHandler handler){
        this.webSocketTransportHandler = handler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketMessageHandler(), "/hello");
        registry.addHandler(new WebSocketBinaryHandler(), "/json");
        registry.addHandler(webSocketTransportHandler, "/stream");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(10485760);
    }
}
