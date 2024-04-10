package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.data.repo.ImageMetaRepository;
import com.lrl.liudrivecore.service.tool.intf.ImageReader;
import com.lrl.liudrivecore.service.tool.intf.ImageSaver;
import com.lrl.liudrivecore.service.tool.stereotype.PathStereotype;
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

    private ImageReader reader;

    private ImageSaver saver;

    private static final int PAGE_SIZE = 100;
    private ImageMetaRepository repository;


    @Autowired
    public ImageService(ImageMetaRepository repository,
                        ImageReader reader,
                        ImageSaver saver) {
        this.reader = reader;
        this.saver = saver;
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
    public ImageMeta upload(ImageFile imageFile) {
        return upload(imageFile.getMeta(), imageFile.getData());
    }

    @Transactional
    public ImageMeta upload(ImageMeta meta, byte[] data) {

        logger.info("ImageService upload(meta): " + meta.toString());

        // load additional attributes
        if (meta.getScale() == null || meta.getScale() > 2) loadImageScale(meta, data);
        PathStereotype.buildUrl(meta);
        meta.setDateCreated(ZonedDateTime.now());

        //Save meta
        if (!saveImageMeta(meta)) {
            logger.error("ImageMeta failed to save.");
            return null;
        } else logger.info("File saved in database");

        // Save original file
        boolean dataSaved = saver.save(meta.getUrl(), data);
        if (dataSaved) {
            logger.info("File saved in file system.");

        } else logger.error("Image data failed to be saved.");

        return repository.getByUrl(meta.getUrl());
    }


    @Transactional
    public boolean uploadBase64(ImageFileBase64 imageFile) {

        ImageMeta meta = imageFile.getMeta();
        logger.info("ImageService upload(meta): " + meta.toString());
        byte[] data = atob(imageFile.getData());

        // load additive attributes
        if (meta.getScale() == null) loadImageScale(meta, data);
        PathStereotype.buildUrl(meta);
        meta.setDateCreated(ZonedDateTime.now());

        // Save meta
        if (!saveImageMeta(meta)) {
            logger.error("ImageMeta failed to save.");
            return false;
        } else logger.info("File saved in database");

        // Save original file
        boolean dataSaved = false;
        if (dataSaved = saver.save(meta.getUrl(), data)) {
            logger.info("File saved in file system.");
        } else logger.error("Image data failed to be saved.");

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
     * @param userId
     * @param pathUrl
     */
    public ImageFileBase64 getBase64(String userId, String pathUrl) {

        ImageMeta meta = repository.getByUrl(pathUrl);
        if (!meta.getUserId().equals(userId)) return null;

        byte[] data = reader.readAll(pathUrl);

        return ImageFileBase64.copy(meta, data);
    }


    /**
     * @param userId
     * @param pathUrl
     */
    public ImageFile get(String userId, String pathUrl) {

        ImageMeta meta = repository.getByUrl(pathUrl);
        if (!meta.getUserId().equals(userId)) return null;

        byte[] data = reader.readAll(pathUrl);

        return ImageFile.copy(meta, data);
    }

    public ImageFile getThumb(String userId, String url) {
        ImageMeta meta = repository.getByUrl(url);
        if (!meta.getUserId().equals(userId)) return null;

        byte[] data = reader.readThumb(url);

        return ImageFile.copy(meta, data);
    }

    public List<ImageMeta> getList(String userId, Integer page) {
        List<ImageMeta> list = repository.findAllByUserId(userId, PageRequest.of(page, PAGE_SIZE));
        return list;
    }

    /**
     * Delete image file
     *
     * @param filename
     * @return
     */
    public boolean delete(String filename) {
        try {
            ImageMeta meta = repository.getByFilename(filename);
            repository.delete(meta);
        } catch (Exception e) {
            logger.error("Failed to delete from the database");
            return false;
        }
        return saver.delete(filename);
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



}
