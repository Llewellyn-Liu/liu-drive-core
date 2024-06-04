package com.lrl.liudrivecore.service.util.intf;

public interface FileReader {

    /**
     * Read all data from a file
     * @param location
     * @return
     */
    byte[] readAll(String location);

    /**
     * Provide a buffer and read into the buffer
     * @param location
     * @param buffer
     * @param start
     * @param end
     * @return
     */
    int bufferRead(String location, byte[] buffer, Integer start, Integer end);
}
