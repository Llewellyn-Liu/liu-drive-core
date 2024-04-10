package com.lrl.liudrivecore.service.tool.intf;

import com.lrl.liudrivecore.data.pojo.MemoBlock;

import java.util.List;

public interface MemoReader extends FileReader {
    List<MemoBlock> getListByUserId(String userId);
}
