package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.tool.intf.ObjectFileSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemObjectSaver implements ObjectFileSaver {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemObjectSaver.class);

    private String root;

    public LocalDriveSystemObjectSaver(String rootDir) {

        this.root = rootDir;

        logger.info("LocalDriveSystemSaver initialed: root path: "+ this.root);
    }


    //Shared in ObjectSaver and ImageSaver module
    @Override
    public boolean save(String locationAddress, byte[] data) {

        String location = locationStrategy( locationAddress);

        Path p = Paths.get(root + File.separator + location);

        if (!Files.exists(p.getParent())) {
            try {
                Files.createDirectories(p.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.info("Default folder for object file not exists. Creating: " + p.getParent().toString());
        }

        File dataOutputPath = new File(root + File.separator + location);
        if (dataOutputPath.exists()) {
            logger.error("File path already occupied: " + dataOutputPath.getAbsolutePath());
            return false;
        } else {

            try {
                dataOutputPath.createNewFile();
                FileOutputStream output = new FileOutputStream(dataOutputPath);
                output.write(data);
                output.close();
                logger.info("Object file already written into file system: " + dataOutputPath.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    private String locationStrategy(String locationAddress) {
        return locationAddress.split(";")[1];
    }


}
