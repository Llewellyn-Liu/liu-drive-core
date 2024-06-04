package com.lrl.liudrivecore.service.stream.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.*;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ObjectMeta;
import com.lrl.liudrivecore.data.repo.*;
import com.lrl.liudrivecore.service.dir.EtagBuilder;
import com.lrl.liudrivecore.service.dir.uploadConfig.LocalDefaultSaveConfiguration;
import com.lrl.liudrivecore.service.dir.url.URLCheck;
import com.lrl.liudrivecore.service.dir.url.URLValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;


@Service
public class WebSocketDataWriter {

    private Logger logger = LoggerFactory.getLogger(WebSocketDataWriter.class);

    private FileDescriptionRepository objectRepository;

    private LocalDriveSystemObjectSaver saver;
    private BufferedOutputStream out;

    private String outputDirectory = "drive";

    private Integer processCount = 0;

    private URLValidator urlValidator;

    @Autowired
    protected WebSocketDataWriter(FileDescriptionRepository objectRepository,
                                  LocalDriveSystemObjectSaver localDriveSystemObjectSaver,
                                  URLValidator validator) {
        this.objectRepository = objectRepository;
        this.saver = localDriveSystemObjectSaver;
        this.urlValidator = validator;
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
        if(desc == null) return false;

        FileOutputStream fos = saver.prepareOutputStream(desc.getMeta().getLocation());
        if(fos != null) out = new BufferedOutputStream(fos);
        else return false;

        return true;
    }

    private FileDescription readAndSaveJsonData(byte[] jsonData) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectDTO objectDTO = mapper.readValue(jsonData, ObjectDTO.class);

        // Processing description
        if(urlValidator.isValid(objectDTO.getUrl(), objectDTO, false)) throw new RuntimeException("Url validator error");
        urlValidator.buildLocation(objectDTO);

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
    private FileDescription save(ObjectDTO objectDTO) {
        FileDescription fd = new FileDescription();
        fd.setUrl(objectDTO.getUrl());
        fd.setTags(objectDTO.getTags());

        fd.setType(objectDTO.getType());
        fd.setSub(List.of());

        // Timestamps
        fd.setMeta(new ObjectMeta());
        fd.getMeta().setDateCreated(ZonedDateTime.now());
        fd.getMeta().setLastModified(ZonedDateTime.now());

        fd.setConfig(objectDTO.getConfig());
        fd.setMeta(objectDTO.getMeta());

        fd = objectRepository.save(fd);
        EtagBuilder.build(fd);
        return objectRepository.save(fd);
    }
}
