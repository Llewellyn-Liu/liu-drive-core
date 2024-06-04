package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.dto.ObjectSecureResponseDTO;
import com.lrl.liudrivecore.data.dto.ObjectSecureResponseDTOWithData;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.pojo.mongo.ImageDescription;
import com.lrl.liudrivecore.service.ObjectService;
import com.lrl.liudrivecore.service.util.record.ObjectRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/v2")
@RestController
public class ThumbAndDigestController {

    private static Logger logger = LoggerFactory.getLogger(ThumbAndDigestController.class);


    private ObjectService service;

    @Autowired
    public ThumbAndDigestController( ObjectService service) {
        this.service = service;
    }


    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.GET)
    public ObjectSecureResponseDTO getUrl(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        FileDescription result = service.getUrlDescription(url);
        if (result == null) {
            response.setStatus(404);
            return null;
        }

        return ObjectSecureResponseDTO.secureCopy(result);
    }


    /**
     * M4.3.4.1 v0.1.5
     * Get image file thumbnail(Content-Type: image/xxx)
     *
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/thumb/{*url}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageThumbnail(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String url) {

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        // Only when urls are used to reach files in the local file system should be set "File.separator"
        ObjectRecord or = service.getThumb(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(or.description().getMeta().getMimeType()));

        // Response header only allows chars encoded within a byte. For instance, chinese characters,
        // are not legal characters in HTTP headers.
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(or.description().getMeta().getFilename(), "UTF-8")); // 设置下载时的文件名
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(or.data(), headers, org.springframework.http.HttpStatus.OK);
        response.setStatus(200);
        return responseEntity;


    }

    /**
     * M4.3.4.2 v0.1.5
     * Get image file thumbnail(Base64)
     *
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/thumb/base64/{*url}", method = RequestMethod.GET)
    public ObjectSecureResponseDTO getImageThumbnailBase64(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        ObjectSecureResponseDTO osr
                = ObjectSecureResponseDTOWithData.secureCopy(service.getImage(url));


        response.setStatus(200);
        return osr;

    }

    /**
     * API M4.3.1 v0.1.5
     * Get User image list
     *
     * @param request
     * @param response
     * @param userId
     * @param page
     * @return
     */
    @RequestMapping(value = "/drive/gallery/{userId}/page/{page}", method = RequestMethod.GET)
    public List<ObjectSecureResponseDTO> getImageMetaListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                                  @PathVariable String userId,
                                                                  @PathVariable Integer page) {
        List<ImageDescription> result = service.getImageDescriptionListOfUser(userId, page);

        if (result == null) {
            response.setStatus(400);
            return null;
        }

        ArrayList<ObjectSecureResponseDTO> rev = new ArrayList<>();
        result.stream().forEach(e -> {
            rev.add(ObjectSecureResponseDTO.secureCopy(e));
        });
        response.setStatus(200);
        return rev;
    }


    /**
     * API M4.3.1 variant v0.1.5
     * Get User image list
     *
     * @param request
     * @param response
     * @param userId
     * @return
     */
    @RequestMapping(value = "/drive/gallery/{userId}", method = RequestMethod.GET)
    public List<ObjectSecureResponseDTO> getImageMetaListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                                  @PathVariable String userId) {
        List<ImageDescription> result = service.getImageDescriptionListOfUser(userId, 0);

        if (result == null) {
            response.setStatus(400);
            return null;
        }

        ArrayList<ObjectSecureResponseDTO> rev = new ArrayList<>();
        result.stream().forEach(e -> {
            rev.add(ObjectSecureResponseDTO.secureCopy(e));
        });
        response.setStatus(200);
        return rev;
    }


}
