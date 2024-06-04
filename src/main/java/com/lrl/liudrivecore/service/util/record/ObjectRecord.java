package com.lrl.liudrivecore.service.util.record;

import com.lrl.liudrivecore.data.dto.ObjectSecureResponseDTO;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;

public record ObjectRecord(ObjectSecureResponseDTO description, byte[] data) {


}
