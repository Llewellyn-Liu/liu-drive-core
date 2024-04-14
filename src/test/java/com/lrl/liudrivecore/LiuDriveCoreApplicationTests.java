package com.lrl.liudrivecore;

import com.lrl.liudrivecore.data.pojo.VideoMeta;
import com.lrl.liudrivecore.data.repo.VideoMetaRepository;
import com.lrl.liudrivecore.service.MediaFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;

@SpringBootTest
class LiuDriveCoreApplicationTests {

    @Autowired
    VideoMetaRepository videoMetaRepository;

    @Autowired
    MediaFileService mediaFileService;

    @Test
    void contextLoads() {
    }

    @Test
    void videoRepositoryTest(){
        VideoMeta vm = videoMetaRepository.getByUrl("lrl123"+"/"+"test.mp4");
        System.out.println(": "+vm);
    }

}
