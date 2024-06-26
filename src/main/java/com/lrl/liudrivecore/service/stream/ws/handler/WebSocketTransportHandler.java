package com.lrl.liudrivecore.service.stream.ws.handler;

import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.schema.Schema;
import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.repo.*;
import com.lrl.liudrivecore.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class WebSocketTransportHandler extends BinaryWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(WebSocketTransportHandler.class);

    private HashMap<String, WebSocketDataWriter> dataWriterRegistry;

    private FileDescriptionRepository repository;

    private Schema schema;

    private ObjectService service;

    private LocalDriveSystemObjectSaver saver;

    @Autowired
    public WebSocketTransportHandler(FileDescriptionRepository repository, LocalDriveSystemObjectSaver saver,
                                     Schema schema, ObjectService service) {

        this.repository = repository;
        this.saver = saver;
        this.schema = schema;
        this.service =service;
        dataWriterRegistry = new HashMap<>();
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

        if ("ACK".equals((String) session.getAttributes().get("status"))) {

            dataWriterRegistry.get(session.getId()).write(message.getPayload().array());

        } else {
            byte[] jsonData = message.getPayload().array();

            try {
                boolean result = dataWriterRegistry.get(session.getId()).register(jsonData);
                if(!result){
                    throw new RuntimeException("WebSocket upload Failed");
                }
            } catch (IOException e) {
                logger.error("WebSocketTransportHandler failed to create a output stream");
                e.printStackTrace();
                session.close();
                return;
            } catch (RuntimeException e){
                e.printStackTrace();
                session.getAttributes().put("status", "ERR");
                session.sendMessage(new TextMessage(e.getMessage()));
                session.close();
            }

            session.getAttributes().put("status", "ACK");
            session.setBinaryMessageSizeLimit(1048576); // See WebSocketBinaryHandler. Without which the session will throw exception before the second call
            session.sendMessage(new TextMessage("ACK"));
        }

    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("WebSocket binary connection established: " + session.getId());
        dataWriterRegistry.put(session.getId(), new WebSocketDataWriter(repository, saver, schema,service));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Websocket binary connection closed: " + session.getId());
        dataWriterRegistry.get(session.getId()).close();
        dataWriterRegistry.remove(session.getId());
    }


    /**
     * Copied from ImageService v0.1.3.5
     *
     * @param meta
     * @return
     */
    private String loadUrl(ObjectFileMeta meta) {
        if (meta.getUrl() != null && !isValid(meta.getUrl())) meta.setUrl(null);

        if (meta.getUrl() == null) {
            meta.setUrl(meta.getUserId() + File.separator + meta.getFilename());
            return meta.getFilename();
        } else {
            String s = Paths.get(meta.getUserId(), meta.getUrl(), meta.getFilename()).toString();
            meta.setUrl(s);
            return s;
        }
    }

    private boolean isValid(String s) {
        if (s == null) return true;
        Path path;
        try {
            path = Paths.get(s);
        } catch (Exception e) {
            return false;
        }

        // If the 1st char is separator, it's dangerous to have access to the root folder.
        // Case like "\\" in the head will also cause exception when use File to create a directory.
        if (path.toString().charAt(0) == File.separatorChar) return false;

        // Even though it is ok to have dots within a dir name, I think it is confusing.
        if (path.getFileName().toString().contains(".")) return false;


        return true;
    }
}
