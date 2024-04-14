package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.pojo.ImageMeta;
import com.lrl.liudrivecore.service.ImageService;
import com.lrl.liudrivecore.service.tool.runtime.DriveRuntime;
import com.lrl.liudrivecore.service.tool.template.ImageFile;
import com.lrl.liudrivecore.service.tool.template.ImageFileBase64;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.ImageUploadTemplate;
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
                           @RequestBody ImageFileBase64 imageFileBase64) {
        logger.info("Upload Image: " + imageFileBase64.getData().substring(0, 100));
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
     * @param userId
     * @param subUrl
     * @return
     */
    @RequestMapping(value = "/{userId}/{subUrl:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(HttpServletRequest request, HttpServletResponse response,
                                           @PathVariable String userId, @PathVariable String subUrl) {
        logger.info("Getting File: " + userId + ", subUrl: " + subUrl);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        ImageFile file = imageService.get(userId, userId + "/" + subUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(subUrl, "UTF-8")); // 设置下载时的文件名
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
     * @param request
     * @param response
     * @param userId
     * @param subUrl
     * @return
     */
    @RequestMapping(value = "/{userId}/{subUrl:.+}/thumbnail/base64", method = RequestMethod.GET)
    public String getImageThumbnailBase64(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String userId, @PathVariable String subUrl) {
        logger.info("Getting File: " + userId + ", subUrl: " + subUrl);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        return imageService.getBase64(userId, userId + "/" + subUrl).getData();

    }

    /**
     * 3 of the 3 ways to get Image: get image file thumbnail(Content-Type: image/xxx)
     *
     * @param request
     * @param response
     * @param userId
     * @param subUrl
     * @return
     */
    @RequestMapping(value = "/{userId}/{subUrl:.+}/thumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageThumbnail(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String userId, @PathVariable String subUrl) {
        logger.info("Getting File: " + userId + ", subUrl: " + subUrl);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        ImageFile file = imageService.getThumb(userId, userId + "/"+ subUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        // Advice by ChatGPT
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(subUrl, "UTF-8")); // 设置下载时的文件名
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(file.getData(), headers, org.springframework.http.HttpStatus.OK);
        return responseEntity;

    }

    @RequestMapping(value = "/{userId}/{subUrl:.+}/thumbnail/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadImageThumbnail(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String userId, @PathVariable String subUrl) {
        logger.info("Getting File: " + userId + ", subUrl: " + subUrl);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        ImageFile file = imageService.getThumb(userId, userId + "/"+ subUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        // Advice by ChatGPT
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(subUrl, "UTF-8")); // 设置下载时的文件名
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(file.getData(), headers, org.springframework.http.HttpStatus.OK);
        return responseEntity;

    }


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
     * @param request
     * @param response
     * @param userId
     * @param subUrl
     * @return
     */
    @RequestMapping(value = "/{userId}/{subUrl:.+}", method = RequestMethod.DELETE)
    public void deleteImage(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable String userId, @PathVariable String subUrl) {
        logger.info("Deleting File: " + userId + ", subUrl: " + subUrl);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        if (!imageService.delete(userId + "/" + subUrl)) {
            response.setStatus(400);
        }

    }



}
