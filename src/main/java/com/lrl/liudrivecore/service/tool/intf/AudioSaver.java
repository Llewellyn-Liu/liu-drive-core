package com.lrl.liudrivecore.service.tool.intf;

public interface AudioSaver extends FileSaver{

    boolean save(String filePathUrl, byte[] data);

    boolean delete(String filePathUrl);

}
