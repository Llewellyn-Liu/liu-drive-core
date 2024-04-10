package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.tool.intf.ObjectFileSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalDriveSystemObjectSaver implements ObjectFileSaver{

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemObjectSaver.class);

    private String defaultDir;

    public LocalDriveSystemObjectSaver() {
defaultDir = "objects";

        logger.info("LocalDriveSystemSaver initialed");
    }


    //Shared in ObjectSaver and ImageSaver module
    @Override
    public boolean save(String filename, byte[] data) {
        System.out.println("LocalDSSaver Reached");
        File defaultFolder = new File(defaultDir);

        if (!defaultFolder.exists()) {
            logger.info("Default folder for object file not exists. Creating: " + defaultFolder.getPath());
            defaultFolder.mkdir();
        }

        File dataOutputPath = new File(defaultFolder.getPath() + File.separator + filename);
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


}
