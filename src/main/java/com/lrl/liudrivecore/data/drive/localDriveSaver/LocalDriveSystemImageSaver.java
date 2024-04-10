package com.lrl.liudrivecore.data.drive.localDriveSaver;

import com.lrl.liudrivecore.service.tool.intf.ImageSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class LocalDriveSystemImageSaver implements ImageSaver {

    private static Logger logger = LoggerFactory.getLogger(LocalDriveSystemImageSaver.class);

    private final static String defaultDir = "image";

    // Location: defaultDif/thumbnailDir
    private final static String thumbnailDir = "thumb";

    private static final Integer THUMBNAIL_SIZE = 256;

    public LocalDriveSystemImageSaver() {
        logger.info("LocalDriveSystemSaver initialed");
    }



    /**
     * @param pathUrl
     * @param base64String Original string which formats like data:image/jpeg;base64,/9j/4AAQSkZJRgABA
     * @return
     */
    @Override
    public boolean save(String pathUrl, String base64String) {
        byte[] data = Base64.getDecoder().decode(base64String.split(",")[1]);
        return save(pathUrl, data);
    }

    @Override
    public boolean save(String filePathString, byte[] data) {

        Path filePath = Paths.get(defaultDir, filePathString);
        if(!(Files.exists(filePath.getParent()))){
            try {
                Files.createDirectories(filePath.getParent());
            } catch (IOException e) {
                logger.error("LocalDriveSystemImageSaver failed to create a folder");
                return false;
            }
        }

        File dataOutputPath = filePath.toFile();
        if(saveAsFile(dataOutputPath, data) == false) return false;

        saveThumbnail(filePathString, data);
        logger.info("Thumbnail saved");

        return true;
    }

    @Override
    public boolean delete(String pathUrl) {
        Path p = Paths.get(defaultDir, pathUrl);
        Path thumbP = Paths.get(defaultDir,thumbnailDir, pathUrl);

        File imagePath = p.toFile();
        File thumbImagePath= thumbP.toFile();
        if (!imagePath.exists() || !thumbImagePath.exists()) return false;

        boolean imageDeleted = imagePath.delete(), thumbImageDeleted = thumbImagePath.delete();
        if(imageDeleted && thumbImageDeleted) return true;
        else throw new RuntimeException("An Error occurred when deleting image file.");
    }


    private boolean saveThumbnail(String pathUrl, byte[] data) {
        Path p = Paths.get(defaultDir, thumbnailDir, pathUrl);


        if (!Files.exists(p.getParent())) {
            logger.info("Default folder for image thumb does not exist. Creating: " + p.getParent().toString());
            try {
                Files.createDirectories(p.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));

            int width = image.getWidth(), height = image.getHeight();
            int objWidth, objHeight;
            if (width < height) {
                objHeight = THUMBNAIL_SIZE;
                objWidth = (int) Math.floor((double) width / height * objHeight);
            } else {
                objWidth = THUMBNAIL_SIZE;
                objHeight = (int) Math.floor((double) height / width * objWidth);
            }

            Image scaledImage = image.getScaledInstance(objWidth, objHeight, Image.SCALE_FAST);
            BufferedImage scaledBufferedImage = new BufferedImage(objWidth, objHeight, BufferedImage.TYPE_INT_RGB);

            Graphics g = scaledBufferedImage.getGraphics();
            g.drawImage(scaledImage, 0, 0, null);
            g.dispose();

            FileOutputStream fos = new FileOutputStream(p.toFile());

            BufferedOutputStream out = new BufferedOutputStream(fos);
            ImageIO.write(scaledBufferedImage, "jpg", out);

            out.close();
            fos.close();
        } catch (IOException e) {
            logger.error("Create image thumbnail failed");
            return false;
        }

        return true;
    }

    private boolean saveAsFile(File file, byte[] data) {

        if (file.exists()) {
            logger.error("File path already occupied: " + file.getAbsolutePath());
            return false;
        } else {

            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream output = new BufferedOutputStream(fos);
                output.write(data);
                output.close();
                logger.info("Data written: " + file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

}
