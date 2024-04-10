package com.lrl.liudrivecore.service.tool.intf;

public interface ImageReader extends FileReader{

    byte[] readThumb(String filename);
}
