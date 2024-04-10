package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.tool.intf.AudioReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemAudioReader implements AudioReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemAudioReader.class);

    private final static String defaultDir = "audio";

    public LocalDriveSystemAudioReader() {
        logger.info("LocalDriveSystemAudioReader initialed");
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
            logger.error("LocalDriveSystemAudioReader: file not found or reading failure: " + pathUrl);
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
            logger.error("LocalDriveSystemAudioReader: file not found or reading failure: " + pathUrl);
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
}
