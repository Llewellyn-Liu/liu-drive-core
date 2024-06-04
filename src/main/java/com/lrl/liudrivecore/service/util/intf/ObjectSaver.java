package com.lrl.liudrivecore.service.util.intf;

import java.io.FileOutputStream;

public interface ObjectSaver extends FileSaver{


    /**
     * For WebSocket upload stream
     * @param location
     * @return
     */
    FileOutputStream prepareOutputStream(String location);

}
