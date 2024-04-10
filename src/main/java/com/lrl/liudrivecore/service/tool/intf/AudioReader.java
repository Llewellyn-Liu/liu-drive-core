package com.lrl.liudrivecore.service.tool.intf;

import java.io.FileOutputStream;

public interface AudioReader extends FileReader{

    FileOutputStream getFileOutputStream(String filePathUrl);
}
