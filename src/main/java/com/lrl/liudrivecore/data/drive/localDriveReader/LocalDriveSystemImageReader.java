package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.tool.intf.ImageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemImageReader implements ImageReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemImageReader.class);

    private final static String defaultDir = "image";

    private final String thumbDir = "thumb";

    public LocalDriveSystemImageReader() {
        logger.info("LocalDriveSystemImageReader initialed");
    }


    /**
     *
     * @param pathUrl
     * @return
     */
    @Override
    public byte[] readAll(String pathUrl) {

        Path p = Paths.get(defaultDir, pathUrl);

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
    public int bufferRead(String filename, byte[] buffer, Integer start, Integer end) {
        return 0;
    }

    @Override
    public byte[] readThumb(String url) {
        Path p = Paths.get(defaultDir, thumbDir, url);
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
}
