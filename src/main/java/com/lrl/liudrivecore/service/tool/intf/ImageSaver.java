package com.lrl.liudrivecore.service.tool.intf;

public interface ImageSaver extends FileSaver{

    boolean save(String filename, String base64DataString);

    boolean save(String filePathString, byte[] data);

    boolean delete(String filename);

}
