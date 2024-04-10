package com.lrl.liudrivecore.service.tool.intf;

public interface FileReader {


    byte[] readAll(String filename);

    int bufferRead(String filename, byte[] buffer, Integer start, Integer end);
}
