package com.lrl.liudrivecore.service.util.intf;

public interface ImageReader extends FileReader{

    byte[] readThumb(String filename);
}
