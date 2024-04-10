package com.lrl.liudrivecore.service.stream.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.pojo.*;
import com.lrl.liudrivecore.data.repo.AudioMetaRepository;
import com.lrl.liudrivecore.data.repo.ImageMetaRepository;
import com.lrl.liudrivecore.data.repo.ObjectFileMetaRepository;
import com.lrl.liudrivecore.data.repo.VideoMetaRepository;
import com.lrl.liudrivecore.service.tool.stereotype.PathStereotype;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.AudioMetaJsonTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.ImageMetaJsonTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.ObjectFileJsonTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.VideoMetaJsonTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class WebSocketDataWriter {

    private Logger logger = LoggerFactory.getLogger(WebSocketDataWriter.class);

    private ObjectFileMetaRepository objectFileMetaRepository;

    private ImageMetaRepository imageMetaRepository;

    private VideoMetaRepository videoMetaRepository;

    private AudioMetaRepository audioMetaRepository;

    private BufferedOutputStream out;

    private String outputDirectory = "objects";

    private Integer processCount = 0;

    @Autowired
    protected WebSocketDataWriter(ObjectFileMetaRepository objectFileMetaRepository,
                                  ImageMetaRepository imageMetaRepository,
                                  VideoMetaRepository videoMetaRepository,
                                  AudioMetaRepository audioMetaRepository) {
        this.objectFileMetaRepository = objectFileMetaRepository;
        this.imageMetaRepository = imageMetaRepository;
        this.videoMetaRepository = videoMetaRepository;
        this.audioMetaRepository = audioMetaRepository;
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

        StructuredFileMeta meta = readAndSaveJsonData(jsonData);

        Path filePath = Paths.get(outputDirectory, meta.getUrl());
        File file = filePath.toFile();
        if (file.exists()) {
            throw new IOException("WebSocketDataWriter: filename already exists.");
        }

        try {
            Files.createDirectories(filePath.getParent());
            file.createNewFile();
        } catch (IOException e) {
            logger.error("WebSocketResourceManager failed to create a file. " + file.getAbsolutePath());
            throw e;
        }

        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("WebSocketResourceManager failed to create a FileOutputStream");
            throw e;
        }
        return true;
    }

    private StructuredFileMeta readAndSaveJsonData(byte[] jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonData);

        if(root.get("type").isNull() || root.get("type").asText().split("/").length != 2){
            throw new RuntimeException("not valid MIME Type");
        }
        String mimeTypePart = root.get("type").asText().split("/")[0];


        StructuredFileMeta rev;
        if(mimeTypePart == null || mimeTypePart.equals("null")){
            logger.error("Not a valid StructuredFileMeta type");
            throw new IOException();
        }
        else if(mimeTypePart.equals(new MimeType("image").getType())) {
            ImageMeta meta = mapper.readValue(jsonData, ImageMetaJsonTemplate.class).getMeta();
            PathStereotype.buildUrl(meta);
            imageMetaRepository.save(meta);
            this.outputDirectory= "image";
            rev = meta;
        }
        else if(mimeTypePart.equals(new MimeType("video").getType())){
            VideoMeta meta = mapper.readValue(jsonData, VideoMetaJsonTemplate.class).getMeta();
            PathStereotype.buildUrl(meta);
            videoMetaRepository.save(meta);
            this.outputDirectory= "video";
            rev = meta;
        }
        else if(mimeTypePart.equals(new MimeType("audio").getType())) {
            AudioMeta meta = mapper.readValue(jsonData, AudioMetaJsonTemplate.class).getMeta();
            PathStereotype.buildUrl(meta);
            audioMetaRepository.save(meta);
            this.outputDirectory= "audio";
            rev = meta;
        }
        else {
            ObjectFileMeta meta = mapper.readValue(jsonData, ObjectFileJsonTemplate.class).getObjectFileMeta();
            PathStereotype.buildUrl(meta);
            objectFileMetaRepository.save(meta);
            this.outputDirectory= "objects";
            rev = meta;
        }

        return rev;

    }


    private boolean isValidUrl(String s) {
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
}
