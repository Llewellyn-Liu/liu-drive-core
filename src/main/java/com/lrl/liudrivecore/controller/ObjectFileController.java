package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.dto.ObjectFileDTO;
import com.lrl.liudrivecore.data.dto.ObjectFileMetaDTO;
import com.lrl.liudrivecore.data.pojo.MemoBlock;
import com.lrl.liudrivecore.data.pojo.ObjectFileMeta;
import com.lrl.liudrivecore.service.ObjectFileService;
import com.lrl.liudrivecore.service.location.DefaultSaveConfiguration;
import com.lrl.liudrivecore.service.tool.runtime.DriveRuntime;
import com.lrl.liudrivecore.service.tool.template.ObjectFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController()
@RequestMapping("/drive")
public class ObjectFileController {

    private ObjectFileService objectFileService;

    private DriveRuntime driveRuntime;

    @Autowired
    ObjectFileController(ObjectFileService service,
                         DriveRuntime driveRuntime) {
        this.objectFileService = service;
        this.driveRuntime = driveRuntime;
    }

    private Logger logger = LoggerFactory.getLogger(ObjectFileController.class);

    @RequestMapping(value = "/object", method = RequestMethod.POST)
    public void uploadFile(HttpServletRequest request, HttpServletResponse response,
                           @RequestBody ObjectFileDTO fileDTO) {

        logger.info("Upload File");

//        if (!driveRuntime.validate(template.getUsername(), template.getToken(), request.getSession().getId())) {
//            response.setStatus(401);
//            return;
//        }

        //Processing
        try {
            boolean result = objectFileService.upload(fileDTO.getMeta(), fileDTO.getData(), fileDTO.getConfiguration());
        } catch (RuntimeException e) {
            response.setStatus(400);
        }


        response.setStatus(204);

    }

    @RequestMapping(value = "/object/form", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadFileTest(HttpServletRequest request, HttpServletResponse response,
                               @RequestPart("meta") ObjectFileMetaDTO meta,
                               @RequestPart("file") MultipartFile file,
                               @RequestPart("conf") DefaultSaveConfiguration configuration) {

        logger.info("Upload File with Param: meta: " + meta);

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Processing
        boolean result = objectFileService.upload(meta.getMeta(), data, configuration);


        if (!result) {
            response.setStatus(400);

        } else {
            response.setStatus(204);
        }

    }

    /**
     * Chat GPT referenced way to download files
     *
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/object/{url}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFile(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {
        logger.info("Getting File: " + url);

        ObjectFile file = objectFileService.get(url);

        if (file == null) {
            response.setStatus(400);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", Paths.get(url).getFileName().toString()); // 设置下载时的文件名

        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(file.getData(), headers, org.springframework.http.HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/object/{url}", method = RequestMethod.DELETE)
    public ResponseEntity<byte[]> deleteFile(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {
        return null;
    }

    @RequestMapping(value = "/object/{userId}/page/{page}", method = RequestMethod.GET)
    public List<ObjectFileMeta> getMetaListOfUserIdAndPage(HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           @PathVariable String userId,
                                                           @PathVariable Integer page) {
        logger.info("Getting File Meta List: " + userId + ", page: " + page);

        List<ObjectFileMeta> list = objectFileService.getList(userId, page);
        for(ObjectFileMeta m: list){
            System.out.println("abc: "+m);
        }
        return list;
    }


    @RequestMapping(value = "/memo/{userId}", method = RequestMethod.GET)
    public List<MemoBlock> getMemoList(HttpServletRequest request,
                                       HttpServletResponse response,
                                       @PathVariable String userId) {
        logger.info("Getting Memo List: " + userId);
        return objectFileService.getMemoList(userId);
    }

    @RequestMapping(value = "/memo", method = RequestMethod.POST)
    public void uploadMemo(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestBody MemoBlock memo) {
        logger.info("Uploading memo: " + memo.getUserId());
        boolean result = objectFileService.uploadMemo(memo);

        if (result) {
            response.setStatus(204);
        } else response.setStatus(400);
    }


}
