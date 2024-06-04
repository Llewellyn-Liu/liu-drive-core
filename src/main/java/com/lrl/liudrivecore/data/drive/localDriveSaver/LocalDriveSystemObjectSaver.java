package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.util.intf.ObjectSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDriveSystemObjectSaver implements ObjectSaver {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemObjectSaver.class);

    private String root;

    public LocalDriveSystemObjectSaver(String rootDir) {

        this.root = rootDir;

        logger.info("LocalDriveSystemSaver initialed: root path: "+ this.root);
    }


    //Shared in ObjectSaver and ImageSaver module
    @Override
    public boolean save(String location, byte[] data, boolean mandatory) {

        String diskPath = locationStrategy( location);

        Path p = Paths.get(root + File.separator + diskPath);

        if (!Files.exists(p.getParent())) {
            try {
                Files.createDirectories(p.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.info("Default folder for object file not exists. Creating: " + p.getParent().toString());
        }

        File dataOutputPath = new File(root + File.separator + diskPath);
        if (dataOutputPath.exists() && !mandatory) {
            logger.error("File path already occupied: " + dataOutputPath.getAbsolutePath());
            return false;
        } else {

            try {
                if(!dataOutputPath.exists()) dataOutputPath.createNewFile();
                FileOutputStream output = new FileOutputStream(dataOutputPath);
                output.write(data);
                output.close();
                logger.info("Object file completely written into file system: " + dataOutputPath.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public boolean delete(String location, boolean mandatory) {
        String diskPath = locationStrategy(location);
        File file = new File(diskPath);
        if(file.exists()) return file.delete();

        return true;
    }

    private String locationStrategy(String locationAddress) {
        return locationAddress.split(";")[1];
    }


    @Override
    public FileOutputStream prepareOutputStream(String location) {
        String diskPath = locationStrategy(location);

        Path p = Paths.get(root + File.separator + diskPath);

        if (!Files.exists(p.getParent())) {
            try {
                Files.createDirectories(p.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.info("Default folder for object file not exists. Creating: " + p.getParent().toString());
        }

        File dataOutputPath = new File(root + File.separator + diskPath);
        if (dataOutputPath.exists()) {
            logger.error("File path already occupied: " + dataOutputPath.getAbsolutePath());
            return null;
        } else {
            try {
                dataOutputPath.createNewFile();
                FileOutputStream output = new FileOutputStream(dataOutputPath);
                return output;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
