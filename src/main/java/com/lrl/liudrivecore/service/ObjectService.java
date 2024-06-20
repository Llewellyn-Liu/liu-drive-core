package com.lrl.liudrivecore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lrl.liudrivecore.data.drive.localDriveReader.LocalDriveSystemImageReader;
import com.lrl.liudrivecore.data.drive.localDriveReader.LocalDriveSystemObjectReader;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemImageSaver;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.*;
import com.lrl.liudrivecore.data.dto.schema.*;
import com.lrl.liudrivecore.data.pojo.mongo.*;
import com.lrl.liudrivecore.data.repo.FileDescriptionRepository;
import com.lrl.liudrivecore.data.repo.ImageDescriptionRepository;
import com.lrl.liudrivecore.data.repo.MemoDescriptionRepository;
import com.lrl.liudrivecore.service.dir.EtagBuilder;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import com.lrl.liudrivecore.service.util.BSonDeserializer;
import com.lrl.liudrivecore.service.util.record.ObjectRecord;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ObjectService {

    private static Logger logger = LoggerFactory.getLogger(ObjectService.class);

    private FileDescriptionRepository repository;
    private ImageDescriptionRepository imageRepository;

    private MemoDescriptionRepository memoRepository;

    private LocalDriveSystemObjectSaver localDriveSystemObjectSaver;

    private LocalDriveSystemObjectReader localDriveSystemObjectReader;

    private LocalDriveSystemImageSaver localDriveSystemImageSaver;

    private LocalDriveSystemImageReader localDriveSystemImageReader;

    private Schema schema;

    private final static Integer PAGE_SIZE = 20;
    private final static Integer GALLERY_SIZE = 50;


    @Autowired
    public ObjectService(FileDescriptionRepository repository,
                         ImageDescriptionRepository imageRepository,
                         MemoDescriptionRepository memoRepository,
                         LocalDriveSystemObjectSaver saver,
                         LocalDriveSystemObjectReader reader,
                         LocalDriveSystemImageSaver imageSaver,
                         LocalDriveSystemImageReader imageReader,
                         Schema schema) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.memoRepository = memoRepository;
        this.localDriveSystemObjectReader = reader;
        this.localDriveSystemObjectSaver = saver;
        this.localDriveSystemImageReader = imageReader;
        this.localDriveSystemImageSaver = imageSaver;

        this.schema = schema;
    }

    /**
     * Handle upload API M4.2.3.2 (v0.1.5)
     * <p>
     * For details of url rules, see validateUrl()
     *
     * @param meta
     * @param config
     * @param path   Path value accepted from POST and PUT endpoints
     * @param data   Not Null, length > 0
     * @return
     */
    public FileDescription upload(ObjectMetaWithTag meta, DefaultSaveConfigurationImpl config, String path, byte[] data) {

        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        ObjectDTO object = new ObjectDTO();
        object.setUrl(path + "/" + meta.getFilename());
        object.setMeta(meta);
        object.setTags(meta.getTags());
        object.setType("object");
        object.setConfig(config);

        return upload(object, path, data);
    }

    /**
     * Handle upload API M4.2.3.1 (v0.1.5)
     * <p>
     * For details of url rules, see validateUrl()
     *
     * @param objectDTO
     * @param path      Path value accepted from POST and PUT endpoints
     * @param data      Not Null, length > 0
     * @return
     */
    public FileDescription upload(ObjectDTO objectDTO, String path, byte[] data) {

        // check and build url
        objectDTO = schema.filterObjectDTO(objectDTO, path, true, HttpMethod.POST);
        if (objectDTO == null) return null;

        // Not zero check
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        // Save data into file
        boolean isDataSaved = false;
        if (objectDTO.getConfig().getDrive().equals("local")) {
            isDataSaved = localDriveSystemObjectSaver.save(objectDTO.getMeta().getLocation(), data, false);
        } else if (objectDTO.getConfig().getDrive().equals("default")) {
            isDataSaved = localDriveSystemObjectSaver.save(objectDTO.getMeta().getLocation(), data, false);
        } else {
            logger.info("Upload to cloud. Unimplemented");
        }

        if (!isDataSaved) {
            throw new RuntimeException("File data not saved correctly");
        }

        FileDescription savedRecord = repository.getFileDescriptionByUrl(objectDTO.getUrl());
        if (savedRecord == null) {
            Path p = Paths.get(objectDTO.getUrl());
            FileDescription c = save(objectDTO, p);
            return c;
        } else {
            logger.error("URL exists in database.");
            return null;
        }

    }

    /**
     * For PUT method when update a resource, API M4.2.5.1
     * <p>
     * For details of url rules, see validateUrl()
     *
     * @param objectDTO
     * @param path      Path value accepted from POST and PUT endpoints
     * @param data      Not Null, length > 0
     * @return
     */
    public FileDescription uploadAndReplace(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO, String path, byte[] data, boolean isDirectory) {

        // check and build url
        objectDTO = schema.filterObjectDTO(objectDTO, path, true, HttpMethod.PUT);
        if (objectDTO == null) return null;

        // If is
        boolean skipDataSave = false;
        if (data == null || data.length == 0) {
            if (!isDirectory) {
                return null;
            }
            logger.info("Data length equals to 0 or data is null, skip data save.");
            skipDataSave = true;
        }

        FileDescription savedRecord = repository.getFileDescriptionByUrl(objectDTO.getUrl());
        boolean modified = false;
        if (savedRecord != null) {
            byte[] savedData = localDriveSystemObjectReader.readAll(savedRecord.getMeta().getLocation());
            if (Objects.equals(savedData, data)) skipDataSave = true;
        }


        if (!skipDataSave) {
            // Save data into file
            boolean isDataSaved = false;
            if (objectDTO.getConfig().getDrive().equals("local")) {
                isDataSaved = localDriveSystemObjectSaver.save(objectDTO.getMeta().getLocation(), data, true);
            } else if (objectDTO.getConfig().getDrive().equals("default")) {
                isDataSaved = localDriveSystemObjectSaver.save(objectDTO.getMeta().getLocation(), data, true);
            } else {
                logger.info("Upload to cloud. Unimplemented");
            }

            if (!isDataSaved) {
                throw new RuntimeException("File data not saved correctly");
            }
            modified = true;
        }


        if (savedRecord == null) {
            Path p = Paths.get(objectDTO.getUrl());
            savedRecord = save(objectDTO, p);
        } else {

            if (!Objects.equals(savedRecord.getMeta(), objectDTO.getMeta())) {
                modified = true;
                savedRecord.setMeta(objectDTO.getMeta());
            }
            if (!Objects.equals(savedRecord.getConfig(), objectDTO.getConfig())) {
                modified = true;
                savedRecord.setConfig(objectDTO.getConfig());
            }
            if (!Objects.equals(savedRecord.getTags(), objectDTO.getTags())) {
                modified = true;
                savedRecord.setTags(objectDTO.getTags());
            }

            if (modified) {
                savedRecord.getMeta().setLastModified(ZonedDateTime.now().toString());
                savedRecord.getMeta().setEtag(null);
                EtagBuilder.build(savedRecord);
                savedRecord = repository.save(savedRecord);
            }
        }
        return savedRecord;

    }

    /**
     * API M4.2.5.2
     * <p>
     * For details of url rules, see validateUrl()
     *
     * @param meta
     * @param config
     * @param path   Path value accepted from POST and PUT endpoints
     * @param data
     * @return
     */
    public FileDescription uploadAndReplace(ObjectMetaWithTag meta, DefaultSaveConfigurationImpl config,
                                            String path, byte[] data, boolean isDirectory) {

        // When upload a object, data must not be empty
        if (!isDirectory && (data == null || data.length == 0)) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        ObjectDTO object = new ObjectDTO();
        object.setUrl(path);
        object.setMeta(meta);
        object.setTags(meta.getTags());
        object.setType(isDirectory ? "directory" : "object");
        object.setConfig(config);

        return uploadAndReplace(object, path, data, isDirectory);
    }

    private FileDescription save(ObjectDTO objectDTO, Path p) {

        // "object" type and "directory" type have different handler
        FileDescription cfd = objectDTO.getType().equals("object") ? save(objectDTO) : saveDir(objectDTO);

        FileDescription cfdCopy = cfd;
        while (p.getParent() != null) {
            String parentUrl = p.getParent().toString().replace('\\', '/');
            FileDescription pfd = repository.getFileDescriptionByUrl(parentUrl);
            if (pfd == null) {
                pfd = FileDescription.createPath(parentUrl);
                pfd.getSub().add(cfd.getSimpleRecord());
                repository.save(pfd);

                cfd = pfd;
                p = p.getParent();
            } else {
                pfd.getSub().add(cfd.getSimpleRecord());
                repository.save(pfd);

                return cfd;
            }
        }


        return cfdCopy;
    }

    /**
     * When using M4.2.3.2 to create a directory
     *
     * @param objectDTO
     * @param path
     * @return
     */
    public FileDescription createDirectory(ObjectDTO objectDTO, String path) {

        //  validation
        objectDTO = schema.filterObjectDTO(objectDTO, path, true, HttpMethod.POST); // Clean unnecessary fields
        if (objectDTO == null) return null;

        FileDescription savedRecord = repository.getFileDescriptionByUrl(objectDTO.getUrl());
        if (savedRecord == null) {
            Path p = Paths.get(objectDTO.getUrl());
            FileDescription c = save(objectDTO, p);
            return c;
        } else {
            logger.error("URL exists in database.");
            return null;
        }

    }

    /**
     * API M4.2.1.1
     *
     * @param imageDTO
     * @param path
     * @param data
     * @return
     */
    public ImageDescription uploadImage(ImageDTO<ImageMeta, ImageSaveConfiguration> imageDTO, String path, byte[] data) {

        imageDTO = schema.filterImageDTO(imageDTO, path, true);
        if (imageDTO == null) return null;

        // Not zero check
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        // Save data into file
        boolean isDataSaved = false;
        if (imageDTO.getConfig().getDrive().equals("local")) {
            isDataSaved = localDriveSystemImageSaver.save(imageDTO.getMeta().getLocation(), data, false);
        } else if (imageDTO.getConfig().getDrive().equals("default")) {
            isDataSaved = localDriveSystemImageSaver.save(imageDTO.getMeta().getLocation(), data, false);
        } else {
            logger.info("Upload to cloud. Unimplemented");
        }

        if (!isDataSaved) {
            throw new RuntimeException("File data not saved correctly");
        }

        ImageDescription savedRecord = imageRepository.getImageDescriptionByUrl(imageDTO.getUrl());
        if (savedRecord == null) {
            imageDTO.getConfig().setScale(generateImageScale(data));
            ImageDescription c = saveImage(imageDTO);
            return c;
        } else {
            logger.error("URL exists in database.");
            return null;
        }

    }

    /**
     * API M4.2.1.2
     *
     * @param meta
     * @param config
     * @param parentUrl
     * @param data
     * @return
     */
    public ImageDescription uploadImage(ImageMetaWithTag meta, ImageSaveConfiguration config, String parentUrl, byte[] data) {
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        ImageDTO<ImageMeta, ImageSaveConfiguration> object = DTOFactory.getDefaultImageDTO();
        object.setUrl(parentUrl + "/" + meta.getFilename());
        object.setMeta(meta);
        object.setTags(meta.getTags());
        object.setType("object");
        object.setConfig(config);
        object.getMeta().setDateCreated(ZonedDateTime.now().toString());

        return uploadImage(object, parentUrl, data);
    }


    /**
     * API M4.2.4.1
     *
     * @param imageDTO
     * @param path
     * @param data
     * @return
     */
    public ImageDescription updateImage(ImageDTO<ImageMeta, ImageSaveConfiguration> imageDTO, String path, byte[] data) {
        // check and build url
        imageDTO = schema.filterImageDTO(imageDTO, path, true);

        // Not zero check
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        // Save data into file
        boolean isDataSaved = false;
        if (imageDTO.getConfig().getDrive().equals("local")) {
            isDataSaved = localDriveSystemImageSaver.save(imageDTO.getMeta().getLocation(), data, true);
        } else if (imageDTO.getConfig().getDrive().equals("default")) {
            isDataSaved = localDriveSystemImageSaver.save(imageDTO.getMeta().getLocation(), data, true);
        } else {
            logger.info("Upload to cloud. Unimplemented");
        }

        if (!isDataSaved) {
            throw new RuntimeException("File data not saved correctly");
        }

        ImageDescription savedRecord = imageRepository.getImageDescriptionByUrl(imageDTO.getUrl());
        if (savedRecord == null) {
            imageDTO.getConfig().setScale(generateImageScale(data));
            ImageDescription c = saveImage(imageDTO);
            return c;
        } else {
            boolean modified = false;
            if (!Objects.equals(savedRecord.getMeta(), imageDTO.getMeta())) {
                modified = true;
                savedRecord.setMeta(imageDTO.getMeta());
            }
            if (!Objects.equals(savedRecord.getConfig(), imageDTO.getConfig())) {
                modified = true;
                savedRecord.setConfig(imageDTO.getConfig());
            }
            if (!Objects.equals(savedRecord.getTags(), imageDTO.getTags())) {
                modified = true;
                savedRecord.setTags(imageDTO.getTags());
            }

            if (modified) {
                savedRecord.getMeta().setLastModified(ZonedDateTime.now().toString());
                savedRecord.getMeta().setEtag(null);
                EtagBuilder.build(savedRecord);
                savedRecord = imageRepository.save(savedRecord);
            }
        }

        return savedRecord;
    }

    /**
     * For frontend display purpose, scale field can use 1 or 2
     *
     * @param data
     * @return
     */
    private int generateImageScale(byte[] data) {
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
            return (double) bi.getWidth() > 1.5 * bi.getHeight() ? 2 : 1;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * API M4.2.4.2
     *
     * @param meta
     * @param config
     * @param parentUrl
     * @param data
     * @return
     */
    public ImageDescription updateImage(ObjectMetaWithTag meta, DefaultSaveConfigurationImpl config, String parentUrl, byte[] data) {
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUrl(parentUrl + "/" + meta.getFilename());
        imageDTO.setMeta(meta);
        imageDTO.setTags(meta.getTags());
        imageDTO.setConfig(config);

        return updateImage(imageDTO, parentUrl, data);
    }

    private ImageDescription saveImage(ImageDTO imageDTO) {

        ImageDescription pfd = imageRepository.getImageDescriptionByUrl(imageDTO.getUrl());
        if (pfd == null) {
            pfd = ImageDescription.createTemplate(imageDTO);
            return imageRepository.save(pfd);
        } else {
            logger.error("Image " + imageDTO.getUrl() + " exists.");
            return null;
        }

    }


    private FileDescription save(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {
        FileDescription fd = new FileDescription();
        fd.setUrl(objectDTO.getUrl());
        fd.setTags(objectDTO.getTags());

        fd.setType(objectDTO.getType());
        fd.setSub(List.of());

        // Timestamps
        fd.setMeta(new ObjectMeta());
        fd.getMeta().setDateCreated(ZonedDateTime.now().toString());
        fd.getMeta().setLastModified(ZonedDateTime.now().toString());

        fd.setConfig(objectDTO.getConfig());
        fd.setMeta(objectDTO.getMeta());

        fd = repository.save(fd);
        EtagBuilder.build(fd);
        return repository.save(fd);
    }

    private FileDescription saveDir(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {
        FileDescription fd = new FileDescription();
        fd.setUrl(objectDTO.getUrl());
        fd.setTags(objectDTO.getTags());
        fd.setType(objectDTO.getType());
        fd.setSub(List.of());

        fd.setMeta(objectDTO.getMeta());
        fd.setConfig(objectDTO.getConfig());

        fd = repository.save(fd);
        return repository.save(fd);
    }


    /**
     * API M4.2.9
     *
     * @param url
     */
    public void remove(String url) {

        FileDescription fd = repository.getFileDescriptionByUrl(url);
        String parentUrl = Paths.get(url).getParent().toString().replace("\\", "/");
        FileDescription pfd = repository.getFileDescriptionByUrl(parentUrl);
        if (pfd == null) {
            throw new RuntimeException("Parent record not found");
        }

        pfd.getSub().remove(fd.getSimpleRecord());
        repository.save(pfd);
        repository.delete(fd);

        localDriveSystemObjectSaver.delete(fd.getMeta().getLocation(), true);
    }

    /**
     * API M4.2.8
     *
     * @param url
     */
    public void removeImage(String url) {
        ImageDescription imd = imageRepository.getImageDescriptionByUrl(url);
        if (imd == null) {
            throw new RuntimeException("Record not found");
        }
        imageRepository.delete(imd);
        localDriveSystemImageSaver.delete(imd.getMeta().getLocation());
    }


    public ObjectRecord getObject(String url) {
        FileDescription fd = repository.getFileDescriptionByUrl(url);
        if (fd == null || !fd.getType().equals("object")) return null;

        byte[] data = localDriveSystemObjectReader.readAll(fd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(fd), data);
    }

    public ObjectRecord getImage(String url) {
        ImageDescription imd = imageRepository.getImageDescriptionByUrl(url);
        if (imd == null) return null;

        byte[] data = localDriveSystemImageReader.readAll(imd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(imd), data);
    }


    public FileDescription getUrlDescription(String url) {
        FileDescription fd = repository.getFileDescriptionByUrl(url);
        return fd;
    }

    public List<ImageDescription> getImageDescriptionListOfUser(String userId, int page) {
        List<ImageDescription> imd = imageRepository.getAllByMetaUserId(userId, PageRequest.of(page, GALLERY_SIZE));
        return imd;
    }

    public ObjectRecord getThumb(String url) {
        ImageDescription imd = imageRepository.getImageDescriptionByUrl(url);
        if (imd == null) return null;

        byte[] data = localDriveSystemImageReader.readThumb(imd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(imd), data);
    }

    public List<FileDescription> getVideosOfUserId(String userId, Integer page) {
        return repository.findAllVideoTypeOfUserId(userId, PageRequest.of(page, GALLERY_SIZE));
    }

    public FileDescription saveWebSocketObjectDTO(ObjectDTO<ObjectMeta, DefaultSaveConfigurationImpl> objectDTO) {
        return save(objectDTO, Paths.get(objectDTO.getUrl()));
    }

    /**
     * API v0.1.5 M4.3.2
     *
     * @param url
     * @return
     */
    public ObjectSecureResponseDTOWithChildInfo getUrlAndChildren(String url) {
        FileDescription fd = repository.getFileDescriptionByUrl(url);
        if (fd == null) return null;

        ObjectSecureResponseDTOWithChildInfo res =
                new ObjectSecureResponseDTOWithChildInfo(ObjectSecureResponseDTO.secureCopy(fd));
        for (FileDescriptionSimpleRecord sr : fd.getSub()) {
            FileDescription cfd = repository.getFileDescriptionByUrl(sr.getUrl());
            if (cfd == null) {
                logger.error("Child not found: " + sr.getUrl());
                continue;
            }
            res.addChild(ObjectSecureResponseDTO.secureCopy(cfd));
        }

        return res;
    }

    public Memo uploadMemo(Memo memo, String userId, boolean isEnforced) {

        memo = schema.filterMemo(memo, userId, isEnforced, HttpMethod.POST);
        if (memo == null) return null;


        memo.setEtag(EtagBuilder.build(memo));
        return memoRepository.save(memo);
    }


    /**
     * Update a record. If doReplace, replace the data directly
     * Otherwise, compare items in memo and do a precise replacement.
     * @param url
     * @param memo
     * @param method
     * @return
     */
    public Memo updateMemo(String url, Memo memo, HttpMethod method) {


        if (method.equals(HttpMethod.PUT)) {
            memo = schema.filterMemo(memo, url, true, HttpMethod.PUT);
        } else {
            memo = schema.filterMemo(memo, url, true, HttpMethod.PATCH);
        }

        // If memo is null, it did not pass filter
        if (memo == null) return null;


        Memo md = memoRepository.findByUrl(url);
        if (method.equals(HttpMethod.PUT)) {
            if (md == null) {
                memo.setEtag(EtagBuilder.build(memo));
                return memoRepository.save(memo);
            } else {
                md.setData(memo.getData());
                md.setEtag(EtagBuilder.build(memo));

                return memoRepository.save(md);
            }
        } else if (method.equals(HttpMethod.PATCH)) {
            if (md == null) return null;
            try{
                compareAndReplace(md, memo);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Patch url: "+ memo.getUrl() + "failed");
                return null;
            }

            md.setEtag(EtagBuilder.build(memo));
            return memoRepository.save(md);
        } else return null;


    }

    private Memo compareAndReplace(Memo oldM, Memo newM) throws IOException {
        byte[] newDataByte = compareAndReplace(BSonDeserializer.getJsonString(oldM.getData()), BSonDeserializer.getJsonString(newM.getData()));

        ObjectMapper mapper = new ObjectMapper();
        oldM.setData(new Binary(newDataByte));
        return oldM;
    }

    /**
     * 手写的，ChatGPT有更好的方案
     * Compare each field from old json string and new json string.
     * Add or update when new value comes. Keep not affected ones.
     *
     * @param oldJson
     * @param newJson
     * @return
     * @throws JsonProcessingException
     */
    private byte[] compareAndReplace(String oldJson, String newJson)  {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode j1 = null, j2 = null;
        try{
            j1 = mapper.readTree(oldJson);
            j2 = mapper.readTree(newJson);
            return mapper.writeValueAsBytes(compareAndReplace(j1, j2));
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private Object compareAndReplace(JsonNode oldJson, JsonNode newJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        if (oldJson.isObject() && newJson.isObject()){
            LinkedHashMap<String, Object> oldMap = mapper.readValue(oldJson.toString(), LinkedHashMap.class);
            LinkedHashMap<String, Object> newMap = mapper.readValue(newJson.toString(), LinkedHashMap.class);
            for (String key : newMap.keySet()) {
                if (!oldMap.containsKey(key)) oldMap.put(key, newMap.get(key));
                else {
                    if(oldJson.get(key).isObject() && newJson.get(key).isObject()){
                        oldMap.put(key, compareAndReplace(oldJson.get(key), newJson.get(key)));
                    }else{
                        oldMap.put(key, newMap.get(key));
                    }
                }
            }

            return oldMap;

        }else return mapper.readValue(newJson.asText(), LinkedHashMap.class);
    }

    public Memo getMemo(String url) {

        return memoRepository.findByUrl(url);
    }

    public List<Memo> getMemosOfUserId(String userId) {
        return memoRepository.findAllByUserId(userId);
    }
}
