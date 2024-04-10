package com.lrl.liudrivecore.service.stream.ws.handler;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WebSocketBinaryHandler extends BinaryWebSocketHandler {

    private BufferedOutputStream out;

    public WebSocketBinaryHandler() {

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

        byte[] data = message.getPayload().array();

        out.write(data);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            out = new BufferedOutputStream(new FileOutputStream(session.getId()+"-output.mp4"));
        } catch (FileNotFoundException e) {
            System.out.println("Exception in WebSocketBinaryHandler");
        }

        session.setBinaryMessageSizeLimit(1048576);
        System.out.println("Binary connection established: "+ session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Binary connection closed: "+ session.getId());
        out.close();
        super.afterConnectionClosed(session, status);
    }
}
