package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.tool.intf.VideoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemVideoReader implements VideoReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemVideoReader.class);

    private final static String defaultDir = "video";

    private final String thumbDir = "thumb";

    public LocalDriveSystemVideoReader() {
        logger.info("LocalDriveSystemVideoReader initialed");
    }

    /**
     *
     * @param pathUrl Labeled as filename but used as pathUrl here
     * @return
     */
    @Override
    public byte[] readAll(String pathUrl) {
        Path p = Paths.get(defaultDir, pathUrl);
        File file = p.toFile();

        try {
            FileInputStream fis = new FileInputStream(file);
            return fis.readAllBytes();
        } catch (FileNotFoundException e) {
            logger.error("LocalDriveSystemVideoReader: file not found or reading failure: " + pathUrl);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public int bufferRead(String pathUrl, byte[] buffer, Integer start, Integer end) {
        Path p = Paths.get(defaultDir, pathUrl);
        File file = p.toFile();

        try {
            FileInputStream fis = new FileInputStream(file);
            return fis.read(buffer, start, end);
        } catch (FileNotFoundException e) {
            logger.error("LocalDriveSystemVideoReader: file not found or reading failure: " + pathUrl);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileOutputStream getFileOutputStream(String filePathUrl) {

        Path p = Paths.get(defaultDir, filePathUrl);
        File file = p.toFile();
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileSystemResource getFileAsResource(String filePathUrl) {
        Path p = Paths.get(defaultDir, filePathUrl);
        File f = p.toFile();
        if(!f.exists()){
            throw new RuntimeException("File" +filePathUrl+" not found in local file system");
        }

        return new FileSystemResource(f);
    }
}
