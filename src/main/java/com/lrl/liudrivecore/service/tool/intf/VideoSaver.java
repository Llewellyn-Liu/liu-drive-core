package com.lrl.liudrivecore.service.tool.intf;

public interface VideoSaver extends FileSaver{

    boolean save(String filePathUrl, byte[] data);

    boolean delete(String filePathUrl);

}
