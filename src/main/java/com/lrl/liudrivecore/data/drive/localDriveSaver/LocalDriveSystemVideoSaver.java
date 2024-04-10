package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.tool.intf.VideoSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemVideoSaver implements VideoSaver {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemVideoSaver.class);

    private final static String defaultDir = "video";

    // Location: defaultDif/thumbnailDir
    private final static String thumbnailDir = "thumb";

    private static final Integer THUMBNAIL_SIZE = 256;

    public LocalDriveSystemVideoSaver() {
        logger.info("LocalDriveSystemSaver initialed");
    }


    @Override
    public boolean save(String filePathString, byte[] data) {
        Path p = Paths.get(defaultDir, filePathString);
        File f = p.toFile();
        if(f.exists()) {
            throw new RuntimeException("LocalDriveSystemVideoSaver: filename already exists.");
        }

        try{
            Files.createDirectories(p.getParent());
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(data);
            bos.close();
        } catch (IOException e) {
            logger.error("LocalDriveSystemVideoSaver: Failed to create folder or file of: "+ filePathString);
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean delete(String filePathUrl) {
        Path p = Paths.get(defaultDir, filePathUrl);

        File f = p.toFile();
        if(!f.exists()) {
            throw new RuntimeException("LocalDriveSystemVideoSaver: filename does not exist.");
        }

        f.delete();

        return true;
    }
}
