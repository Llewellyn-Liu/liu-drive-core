package com.lrl.liudrivecore.data.drive.localDriveReader;

import com.lrl.liudrivecore.service.util.intf.ObjectFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemObjectReader implements ObjectFileReader {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemObjectReader.class);

    private String rootDir;

    public LocalDriveSystemObjectReader(String rootDir) {
        this.rootDir = rootDir;

        logger.info("LocalDriveSystemReader initialized.");
    }


    @Override
    public byte[] readAll(String location) {
        String diskPath = locationStrategy(location);

        Path p = Paths.get(rootDir, diskPath);

        File fileDir = p.getParent().toFile();
        if (!fileDir.exists()) return null;

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

    private String locationStrategy(String locationAddress) {
        return locationAddress.split(";")[1];
    }
}
