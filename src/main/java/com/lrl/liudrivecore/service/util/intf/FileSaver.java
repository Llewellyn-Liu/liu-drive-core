package com.lrl.liudrivecore.service.util.intf;

public interface FileSaver {

    boolean save(String location, byte[] data, boolean mandatory);

    boolean delete(String location, boolean mandatory);

}
