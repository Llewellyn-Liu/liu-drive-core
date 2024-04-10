package com.lrl.liudrivecore.service.tool.intf;

import com.lrl.liudrivecore.data.pojo.MemoBlock;

public interface MemoSaver extends FileSaver{

    boolean save(MemoBlock memo);

}
