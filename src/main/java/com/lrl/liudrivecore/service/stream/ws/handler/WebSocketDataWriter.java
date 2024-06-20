package com.lrl.liudrivecore.service.stream.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.AbstractObjectDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.dto.schema.Schema;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.data.repo.*;
import com.lrl.liudrivecore.service.ObjectService;
import com.lrl.liudrivecore.service.dir.EtagBuilder;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.List;


@Service
public class WebSocketDataWriter {

    private Logger logger = LoggerFactory.getLogger(WebSocketDataWriter.class);

    private FileDescriptionRepository objectRepository;

    private LocalDriveSystemObjectSaver saver;

    private ObjectService objectService;
    private BufferedOutputStream out;

    private String outputDirectory = "drive";

    private Integer processCount = 0;

    private Schema schema;

    @Autowired
    protected WebSocketDataWriter(FileDescriptionRepository objectRepository,
                                  LocalDriveSystemObjectSaver localDriveSystemObjectSaver,
                                  Schema schema,
                                  ObjectService service) {
        this.objectRepository = objectRepository;
        this.saver = localDriveSystemObjectSaver;
        this.schema = schema;
        this.objectService = service;
        out = null;
    }

    protected void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public boolean register(byte[] jsonData) throws IOException {

        //test
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonData);
        System.out.println(jsonNode);

        FileDescription desc = readAndSaveJsonData(jsonData);
        if (desc == null) return false;

        FileOutputStream fos = saver.prepareOutputStream(desc.getMeta().getLocation());
        if (fos != null) out = new BufferedOutputStream(fos);
        else return false;

        return true;
    }

    private FileDescription readAndSaveJsonData(byte[] jsonData) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO = mapper.readValue(jsonData, ObjectDTO.class);

        // json completion
        objectDTO.setType("object");

        // Url check
        objectDTO = schema.filterObjectDTO(objectDTO, objectDTO.getMeta().getUserId(), true, HttpMethod.POST);
        if(objectDTO == null) return null;

        return save(objectDTO);
    }


    public void write(byte[] data) {
        try {
            out.write(data);
            processCount++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Integer getProcessCount() {
        return processCount;
    }

    /**
     * Copied from ObjectService v0.1.5
     *
     * @param objectDTO
     * @return
     */
    private FileDescription save(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {
        return objectService.saveWebSocketObjectDTO(objectDTO);
    }
}
