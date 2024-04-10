package com.lrl.liudrivecore.service.tool.intf;

import org.springframework.core.io.FileSystemResource;

import java.io.FileOutputStream;

public interface VideoReader extends FileReader{

    FileOutputStream getFileOutputStream(String filePathUrl);

    FileSystemResource getFileAsResource(String filePathUrl);
}
