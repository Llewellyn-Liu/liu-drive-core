package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.dto.ImageFileBase64ModuleDTO;
import com.lrl.liudrivecore.data.dto.ImageFileBase64ModuleDTOTest;
import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.service.ImageService;
import com.lrl.liudrivecore.service.location.URLCheck;
import com.lrl.liudrivecore.service.tool.runtime.DriveRuntime;
import com.lrl.liudrivecore.service.tool.template.ImageFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/drive/image")
public class ImageController {

    private static Logger logger = LoggerFactory.getLogger(ImageController.class);

    private DriveRuntime driveRuntime;

    private ImageService imageService;

    @Autowired
    public ImageController(DriveRuntime driveRuntime,
                           ImageService imageService) {
        this.driveRuntime = driveRuntime;
        this.imageService = imageService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void uploadFile(HttpServletRequest request, HttpServletResponse response,
                           @RequestBody ImageFileBase64ModuleDTO imageFileBase64) {
        logger.info("Upload Image: " + imageFileBase64.getMeta());

//        if (!driveRuntime.validate(template.getUsername(), template.getToken(), request.getSession().getId())) {
//            response.setStatus(401);
//            return;
//        }


        //Processing
        boolean result = imageService.uploadBase64(imageFileBase64.getMeta(), imageFileBase64.getData(), imageFileBase64.getConfiguration());

        if (!result) {
            response.setStatus(400);

        } else {
            response.setStatus(204);
        }

    }


    /**
     * 1 of the 3 ways to get Image: get original image file(Content-Type: image/xxx)
     *
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/{*url}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(HttpServletRequest request, HttpServletResponse response,
                                           @PathVariable String url) {

        // Remove the start "/"
        url = url.replaceFirst("^/", "");
        logger.info("Getting File: " + url);

        ImageFile file = imageService.get(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(file.getFilename(), "UTF-8")); // 设置下载时的文件名
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(file.getData(), headers, org.springframework.http.HttpStatus.OK);
        return responseEntity;
    }


    /**
     * 2 of the 3 ways to get Image: get image file thumbnail(Base64)
     *
     * Moved to "/drive/thumb"
     */


    /**
     * 3 of the 3 ways to get Image: get image file thumbnail(Content-Type: image/xxx)
     *
     * Moved to "/drive/thumb"
     */



    @RequestMapping(value = "/{userId}/page/{page}", method = RequestMethod.GET)
    public List<ImageMeta> getImageMetaListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String userId,
                                                    @PathVariable Integer page) {
        logger.info("Getting user ImageMeta list of page" + page);

        return imageService.getList(userId, page);
    }

    /**
     * 1 of the 3 ways to get Image: get original image file(Content-Type: image/xxx)
     *
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/{*url}", method = RequestMethod.DELETE)
    public void deleteImage(HttpServletResponse response,
                            @PathVariable String url) {

        url = URLCheck.readUrlFromRequestPath(url);
        logger.info("Deleting File: " + url);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        if (!imageService.delete(url)) {
            response.setStatus(400);
        }

    }



}
