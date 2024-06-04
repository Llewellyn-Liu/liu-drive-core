package com.lrl.liudrivecore.service.dir.url;

import com.lrl.liudrivecore.data.dto.ImageDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;

public interface URLValidator {

    /**
     * @param path
     * @param objectDTO
     * @param isPartial If PATH represents a part of the url (e.g. POST methods), or full url (in PUT methods)
     * @return
     */
    boolean isValid(String path, ObjectDTO objectDTO, boolean isPartial);

    boolean isValidImageUrl(String path, ImageDTO imageDTO, boolean isPartial);

    boolean isValidDirectoryUrl(ObjectDTO objectDTO, String path, boolean isPartial);

    String buildUrl(ObjectDTO objectDTO, String validPath);

    void buildLocation(ObjectDTO objectDTO);

}
