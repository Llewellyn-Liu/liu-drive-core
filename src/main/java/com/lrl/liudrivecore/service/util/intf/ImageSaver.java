package com.lrl.liudrivecore.service.util.intf;

public interface ImageSaver extends FileSaver{

    boolean save(String filename, String base64DataString, boolean mandatory);

    boolean delete(String filename);

}
