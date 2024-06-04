package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.drive.localDriveReader.LocalDriveSystemImageReader;
import com.lrl.liudrivecore.data.drive.localDriveReader.LocalDriveSystemObjectReader;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemImageSaver;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemObjectSaver;
import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.dto.ObjectSecureResponseDTO;
import com.lrl.liudrivecore.data.pojo.mongo.*;
import com.lrl.liudrivecore.data.repo.FileDescriptionRepository;
import com.lrl.liudrivecore.data.repo.ImageDescriptionRepository;
import com.lrl.liudrivecore.service.dir.EtagBuilder;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.url.URLValidator;
import com.lrl.liudrivecore.service.util.record.ObjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ObjectService {

    private static Logger logger = LoggerFactory.getLogger(ObjectService.class);

    private FileDescriptionRepository repository;
    private ImageDescriptionRepository imageRepository;

    private LocalDriveSystemObjectSaver localDriveSystemObjectSaver;

    private LocalDriveSystemObjectReader localDriveSystemObjectReader;

    private LocalDriveSystemImageSaver localDriveSystemImageSaver;

    private LocalDriveSystemImageReader localDriveSystemImageReader;

    private URLValidator urlValidator;

    private final static Integer PAGE_SIZE = 20;
    private final static Integer GALLERY_SIZE = 50;


    @Autowired
    public ObjectService(FileDescriptionRepository repository,
                         ImageDescriptionRepository imageRepository,
                         LocalDriveSystemObjectSaver saver,
                         LocalDriveSystemObjectReader reader,
                         LocalDriveSystemImageSaver imageSaver,
                         LocalDriveSystemImageReader imageReader,
                         URLValidator urlValidator) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.localDriveSystemObjectReader = reader;
        this.localDriveSystemObjectSaver = saver;
        this.localDriveSystemImageReader = imageReader;
        this.localDriveSystemImageSaver = imageSaver;

        this.urlValidator = urlValidator;
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
        validateUrl(path, objectDTO, true);

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
    public FileDescription uploadAndReplace(ObjectDTO objectDTO, String path, byte[] data, boolean isDirectory) {

        // check and build url
        validateUrl(path, objectDTO, true);

        // Not zero check
        boolean skipDataSave = false;
        if (!isDirectory && (data == null || data.length == 0)) {
            logger.info("Data length equals to 0 or data is null, skip data save.");
            skipDataSave = true;
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
        }


        FileDescription savedRecord = repository.getFileDescriptionByUrl(objectDTO.getUrl());
        FileDescription c;
        if (savedRecord == null) {
            Path p = Paths.get(objectDTO.getUrl());
            c = save(objectDTO, p);
        } else {
            savedRecord.setMeta(objectDTO.getMeta());
            savedRecord.setConfig(objectDTO.getConfig());
            savedRecord.setType(objectDTO.getType());
            savedRecord.setTags(objectDTO.getTags());
            savedRecord.getMeta().setLastModified(ZonedDateTime.now());

            c = repository.save(savedRecord);
        }

        return c;
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

        // quick validation
        boolean isValid = urlValidator.isValidDirectoryUrl(objectDTO, path, true);
        if (!isValid) {
            return null;
        }

        objectDTO = secureObjectDTOForDirectory(objectDTO); // Clean unnecessary fields

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
    public ImageDescription uploadImage(ImageDTO imageDTO, String path, byte[] data) {

        // check and build url
        validateImageUrl(path, imageDTO, true);

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
    public ImageDescription uploadImage(ObjectMetaWithTag meta, DefaultSaveConfigurationImpl config, String parentUrl, byte[] data) {
        if (data == null || data.length == 0) {
            logger.error("Data length equals to 0 or data is null");
            return null;
        }

        ImageDTO object = new ImageDTO();
        object.setUrl(parentUrl + "/" + meta.getFilename());
        object.setMeta(meta);
        object.setTags(meta.getTags());
        object.setType("object");
        object.setConfig(config);

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
    public ImageDescription updateImage(ImageDTO imageDTO, String path, byte[] data) {
        // check and build url
        validateImageUrl(path, imageDTO, false);

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
            ImageDescription c = saveImage(imageDTO);
            return c;
        } else {
            logger.error("URL exists in database.");
            return null;
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

    private ObjectDTO secureObjectDTOForDirectory(ObjectDTO objectDTO) {

        // For Meta
        ObjectMeta meta = objectDTO.getMeta();
        meta.setLocation(null); // Directory does not need a location
        meta.setEtag(null);
        meta.setAuthor(null);
        meta.setFilename(null);
        meta.setMimeType(null);

        return objectDTO;
    }


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

        fd = repository.save(fd);
        EtagBuilder.build(fd);
        return repository.save(fd);
    }

    private FileDescription saveDir(ObjectDTO objectDTO) {
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
     * Url validator for generating lawful urls.
     * According to design, POST allow incomplete upload path(then take json.url as reference)
     * PUT allow empty json.url
     *
     * @param path      path value from controller endpoints.
     * @param json
     * @param isPartial if PATH represents a part of the url (eg. POST methods), or full url (in PUT methods)
     */
    public void validateUrl(String path, ObjectDTO json, boolean isPartial) {

        if (!urlValidator.isValid(path, json, isPartial)) {
            json.setUrl(null);
            urlValidator.buildUrl(json, path);
        }

        urlValidator.buildLocation(json);

    }

    public void validateImageUrl(String path, ImageDTO imageDTO, boolean isPost) {

        if (!urlValidator.isValidImageUrl(path, imageDTO, isPost)) {
            imageDTO.setUrl(null);
            urlValidator.buildUrl(imageDTO, imageDTO.getMeta().getUserId());
        }

        urlValidator.buildLocation(imageDTO);


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
        if (!fd.getType().equals("object")) return null;

        byte[] data = localDriveSystemObjectReader.readAll(fd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(fd), data);
    }

    public ObjectRecord getImage(String url) {
        ImageDescription imd = imageRepository.getImageDescriptionByUrl(url);
        byte[] data = localDriveSystemImageReader.readAll(imd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(imd), data);
    }


    public FileDescription getUrlDescription(String url) {
        FileDescription fd = repository.getFileDescriptionByUrl(url);
        return repository.getFileDescriptionByUrl(url);
    }

    public List<ImageDescription> getImageDescriptionListOfUser(String userId, int page) {
        List<ImageDescription> imd = imageRepository.getAllByMetaUserId(userId, PageRequest.of(page, GALLERY_SIZE));
        return imd;
    }

    public ObjectRecord getThumb(String url) {
        ImageDescription imd = imageRepository.getImageDescriptionByUrl(url);
        byte[] data = localDriveSystemImageReader.readThumb(imd.getMeta().getLocation());
        return new ObjectRecord(ObjectSecureResponseDTO.secureCopy(imd), data);
    }
}
