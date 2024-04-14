package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.tool.intf.ObjectFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LocalDriveSystemObjectReader implements ObjectFileReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemObjectReader.class);

    private String defaultDir;

    public LocalDriveSystemObjectReader(String rootDir) {
        defaultDir = rootDir;

        logger.info("LocalDriveSystemReader initialized.");
    }


    @Override
    public byte[] readAll(String location) {
        File fileDir = new File(location);
        if (!fileDir.exists()) return null;

        byte[] data;
        try {
            FileInputStream inputStream = new FileInputStream(fileDir.getPath());
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
}
