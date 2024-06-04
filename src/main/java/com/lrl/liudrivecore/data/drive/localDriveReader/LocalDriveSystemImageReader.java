package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.util.intf.ImageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemImageReader implements ImageReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemImageReader.class);

    private String root;

    private final static String thumbDir = "thumb";

    public LocalDriveSystemImageReader(String rootDir) {
        root = rootDir;
        logger.info("LocalDriveSystemImageReader initialed");
    }


    /**
     *
     * @param location
     * @return
     */
    @Override
    public byte[] readAll(String location) {
        String diskPath = locationStrategy(location);
        Path p = Paths.get(root, diskPath);

        File defaultFolder = p.getParent().toFile();
        if (!defaultFolder.exists()) return null;

        byte[] data;
        try {
            FileInputStream inputStream = new FileInputStream(p.toFile());
            data = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public int bufferRead(String location, byte[] buffer, Integer start, Integer end) {
        return 0;
    }

    @Override
    public byte[] readThumb(String location) {

        String diskPath = locationStrategy(location);
        Path p = Paths.get(root, thumbDir, diskPath);
        File defaultFolder = p.getParent().toFile();
        if (!defaultFolder.exists()) return null;

        byte[] data;
        try {
            FileInputStream inputStream = new FileInputStream(p.toFile());
            data = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    private String locationStrategy(String locationAddress) {
        return locationAddress.split(";")[1];
    }
}
