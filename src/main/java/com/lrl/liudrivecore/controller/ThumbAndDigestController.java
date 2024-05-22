package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.service.ImageService;
import com.lrl.liudrivecore.service.tool.runtime.DriveRuntime;
import com.lrl.liudrivecore.service.tool.template.ImageFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RequestMapping(value = "/drive")
@RestController
public class ThumbAndDigestController {

    private static Logger logger = LoggerFactory.getLogger(ThumbAndDigestController.class);

    private DriveRuntime driveRuntime;

    private ImageService imageService;

    @Autowired
    public ThumbAndDigestController(DriveRuntime driveRuntime, ImageService service){
        this.driveRuntime = driveRuntime;
        this.imageService = service;
    }

    /**
     * Get image file thumbnail(Content-Type: image/xxx)
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/thumb/{*url}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageThumbnail(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String url) {

        // Remove the start "/"
        url = url.replaceFirst("^/", "");
        logger.info("Getting File: " + url);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        ImageFile file = imageService.getThumb(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        // Advice by ChatGPT
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
     * Get image file thumbnail(Base64)
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/thumb/base64/{*url}", method = RequestMethod.GET)
    public String getImageThumbnailBase64(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {

        // Remove the start "/"
        url = url.replaceFirst("^/", "");
        logger.info("Getting File: " + url);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        return imageService.getThumbBase64(url).getData();

    }


}
