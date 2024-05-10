package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.drive.localDriveReader.LocalDriveSystemImageReader;
import com.lrl.liudrivecore.data.drive.localDriveSaver.LocalDriveSystemImageSaver;
import com.lrl.liudrivecore.data.dto.ImageFileDTO;
import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.data.repo.ImageMetaRepository;
import com.lrl.liudrivecore.service.location.DefaultSaveConfiguration;
import com.lrl.liudrivecore.service.location.URLCheck;
import com.lrl.liudrivecore.service.tool.template.ImageFile;
import com.lrl.liudrivecore.service.tool.template.ImageFileBase64;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ImageService {

    private static Logger logger = LoggerFactory.getLogger(ImageService.class);

    private LocalDriveSystemImageReader localDriveSystemImageReader;

    private LocalDriveSystemImageSaver localDriveSystemImageSaver;

    private static final int PAGE_SIZE = 100;
    private ImageMetaRepository repository;


    @Autowired
    public ImageService(ImageMetaRepository repository,
                        LocalDriveSystemImageReader reader,
                        LocalDriveSystemImageSaver saver) {
        this.localDriveSystemImageReader = reader;
        this.localDriveSystemImageSaver = saver;
        this.repository = repository;
    }


    /**
     * Upload progress:
     * 1. Get scale of image data, write into ImageMeta
     * 2. Save ImageMeta into database
     * 3. Save Image original data(including thumbnail)
     *
     * @param imageFile
     * @return
     */
    @Transactional
    public ImageMeta upload(ImageFileDTO imageFile) {
        return upload(imageFile.getMeta(), imageFile.getData(), imageFile.getConfiguration());
    }

    @Transactional
    public ImageMeta upload(ImageMeta meta, byte[] data, DefaultSaveConfiguration configuration) {

        logger.info("ImageService upload(meta): " + meta.toString());

        // load additional attributes
        if (meta.getScale() == null || meta.getScale() > 2) loadImageScale(meta, data);
        URLCheck.buildUrl(meta, configuration);
        meta.setDateCreated(ZonedDateTime.now());

        // Save original file
        boolean dataSaved = saveData(meta.getLocation(), data);
        if (dataSaved) {
            logger.info("File saved in file system.");

        } else logger.error("Image data failed to be saved.");

        // Encrypt location
        meta.setLocation(URLCheck.encrypt(meta.getLocation()));

        //Save meta
        if (!saveImageMeta(meta)) {
            logger.error("ImageMeta failed to save.");
            return null;
        } else logger.info("File saved in database");


        return repository.getByUrl(meta.getUrl());
    }


    @Transactional
    public boolean uploadBase64(ImageMeta meta, String dataBase64, DefaultSaveConfiguration configuration) {

        logger.info("ImageService upload(meta): " + meta.toString());
        byte[] data = atob(dataBase64);

        // load additive attributes
        if (meta.getScale() == null || meta.getScale() > 2) loadImageScale(meta, data);

        URLCheck.buildUrl(meta, configuration);
        meta.setDateCreated(ZonedDateTime.now());

        // Save original file
        boolean dataSaved;
        if (dataSaved = saveData(meta.getLocation(), data)) {
            logger.info("File saved in file system.");
        } else logger.error("Image data failed to be saved.");

        // Encrypt location
        meta.setLocation(URLCheck.encrypt(meta.getLocation()));

        // Save meta
        if (!saveImageMeta(meta)) {
            logger.error("ImageMeta failed to save.");
            return false;
        } else logger.info("File saved in database");


        return dataSaved;
    }

    private boolean saveImageMeta(ImageMeta meta) {
        if (repository.getByFilename(meta.getFilename()) != null) {
            logger.info("Filename exists in database.");
            return false;
        }

        try {
            repository.save(meta);
        } catch (Exception e) {
            return false;
        }

        return true;

    }

    /**
     * Get Image:
     * - Use special methods to download thumbnails.
     */

    /**
     * @param url
     */
    public ImageFileBase64 getThumbBase64(String url) {

        ImageMeta meta = repository.getByUrl(url);
        byte[] data = readData(meta, true);

        return ImageFileBase64.copy(meta, data);
    }


    /**
     * @param url
     * @return
     */
    public ImageFile get(String url) {

        ImageMeta meta = repository.getByUrl(url);

        byte[] data = readData(meta, false);

        return ImageFile.copy(meta, data);
    }


    public ImageFile getThumb(String url) {
        ImageMeta meta = repository.getByUrl(url);

        System.out.println("Debug:"+meta);
        byte[] data = readData(meta, true);

        return ImageFile.copy(meta, data);
    }

    public List<ImageMeta> getList(String userId, Integer page) {
        List<ImageMeta> list = repository.findAllByUserId(userId, PageRequest.of(page, PAGE_SIZE));
        return list;
    }

    /**
     * Delete image file
     *
     * @param url
     * @return
     */
    public boolean delete(String url) {

        System.out.println("Debug: delete url: "+url);
        try {
            ImageMeta meta = repository.getByUrl(url);
            repository.delete(meta);

            return deleteData(meta);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to delete from the database");
            return false;
        }
    }

    /**
     * Read the location address and distribute the flow
     * Data should be saved before database, which need encryption
     *
     * @param location
     * @param data
     * @return
     */
    private boolean saveData(String location, byte[] data) {

        if (location.startsWith("local")) {
            return localDriveSystemImageSaver.save(location.split(";")[1], data);
        } else if (location.startsWith("cloud")) {

        } else {

        }

        return false;
    }

    /**
     * @param meta
     */
    private byte[] readData(ImageMeta meta, boolean isThumbnail) {

        String location = URLCheck.decrypt(meta.getLocation());

        System.out.println("Debug:"+location);
        if (location.startsWith("local")) {
            location = location.split(";")[1];
            return isThumbnail ? localDriveSystemImageReader.readThumb(location)
                    : localDriveSystemImageReader.readAll(location);
        } else if (location.startsWith("cloud")) {

        } else {

        }

        return null;
    }


    /**
     * @param meta
     */
    private boolean deleteData(ImageMeta meta) {

        String location = URLCheck.decrypt(meta.getLocation());
        if (location.startsWith("local")) {
            location = location.split(";")[1];
            return localDriveSystemImageSaver.delete(location);
        } else if (location.startsWith("cloud")) {

        } else {

        }

        return false;
    }


    private byte[] atob(String base64String) {
        return Base64.getDecoder().decode(base64String.split(",")[1]);
    }

    /**
     * This method will generate a default scale for certain image to have a better ratio when
     * displayed in browser.
     * Ratio equals length / height
     *
     * @param meta
     * @param data
     */
    private void loadImageScale(ImageMeta meta, byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try {
            BufferedImage image = ImageIO.read(inputStream);
            int width = image.getWidth(), height = image.getHeight();
            meta.setScale((double) width / height > 1.5 ? 2 : 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    public void reset(){

    }

}
