package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.tool.intf.AudioSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemAudioSaver implements AudioSaver {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemAudioSaver.class);

    private final static String defaultDir = "audio";

    // Location: defaultDif/thumbnailDir
    private final static String thumbnailDir = "thumb";

    private static final Integer THUMBNAIL_SIZE = 256;

    public LocalDriveSystemAudioSaver() {
        logger.info("LocalDriveSystemSaver initialed");
    }


    @Override
    public boolean save(String filePathString, byte[] data) {
        Path p = Paths.get(defaultDir, filePathString);
        File f = p.toFile();
        if(f.exists()) {
            throw new RuntimeException("LocalDriveSystemAudioSaver: filename already exists.");
        }

        try{
            Files.createDirectories(p.getParent());
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(data);
            bos.close();
        } catch (IOException e) {
            logger.error("LocalDriveSystemAudioSaver: Failed to create folder or file of: "+ filePathString);
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean delete(String filePathUrl) {
        Path p = Paths.get(defaultDir, filePathUrl);

        File f = p.toFile();
        if(!f.exists()) {
            throw new RuntimeException("LocalDriveSystemAudioSaver: filename does not exist.");
        }

        f.delete();

        return true;
    }
}
