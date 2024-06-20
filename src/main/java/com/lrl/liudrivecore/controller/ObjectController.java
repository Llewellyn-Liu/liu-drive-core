package com.lrl.liudrivecore.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrl.liudrivecore.data.dto.*;
import com.lrl.liudrivecore.data.pojo.MemoBlock;
import com.lrl.liudrivecore.data.pojo.mongo.*;
import com.lrl.liudrivecore.service.ObjectService;
import com.lrl.liudrivecore.service.dir.uploadConfig.DefaultSaveConfigurationImpl;
import com.lrl.liudrivecore.service.dir.uploadConfig.ImageSaveConfiguration;
import com.lrl.liudrivecore.service.util.record.ObjectRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@CrossOrigin(value = "http://localhost:5173")
@RequestMapping(value = "/v2")
public class ObjectController {

    private Logger logger = LoggerFactory.getLogger(ObjectController.class);

    private ObjectService objectService;


    @Autowired
    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;

    }

    /**
     * API 4.2.3.1 v0.1.5
     *
     * @param request
     * @param response
     * @param meta
     * @param file
     * @param config
     * @param url      parent folder
     * @return
     */
    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectSecureResponseDTO uploadFile(HttpServletRequest request, HttpServletResponse response,
                                              @RequestPart("meta") ObjectMetaWithTag meta,
                                              @RequestPart("file") MultipartFile file,
                                              @RequestPart("config") DefaultSaveConfigurationImpl config,
                                              @PathVariable("url") String url) {

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);
        // Cases like POST/GET/PUT/DELETE /drive/image/path/ (because the last "/") will be linked /drive
        if (url.startsWith("image/")) {
            response.setStatus(400);
        }

        // Processing
        FileDescription fileDescription = objectService.upload(meta, config, url, data);
        boolean result = fileDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(fileDescription);

    }

    /**
     * API 4.2.3.2 v0.1.5
     *
     * @param request
     * @param response
     * @param json
     * @param path     parent folder
     * @return
     */
    @RequestMapping(value = "/drive/{*path}", method = RequestMethod.POST)
    public ObjectSecureResponseDTO uploadFilePureJson(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody ObjectDTOWithData json,
                                                      @PathVariable("path") String path) {

        // Protection Code
        if (path.startsWith("/")) path = path.substring(1);
        // Cases like POST/GET/PUT/DELETE /drive/image/path/ (because the last "/") will be linked /drive
        if (path.startsWith("image/")) {
            response.setStatus(400);
        }

        // Processing

        FileDescription fileDescription = null;
        if (json.getType().equals("object")) {
            if (json.getData() == null) {
                response.setStatus(400);
                return null;
            }

            json.setData(Base64.getEncoder().encodeToString(json.getData().getBytes()));
            fileDescription = objectService.upload(json, path, json.getData().getBytes());
        } else if (json.getType().equals("directory")) {
            fileDescription = objectService.createDirectory(json, path);
        }

        boolean result = fileDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(fileDescription);

    }

    /**
     * API 4.2.5.1 v0.1.5
     *
     * @param request
     * @param response
     * @param meta
     * @param file
     * @param config
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectSecureResponseDTO updateFile(HttpServletRequest request, HttpServletResponse response,
                                              @RequestPart("meta") ObjectMetaWithTag meta,
                                              @RequestPart("file") MultipartFile file,
                                              @RequestPart("config") DefaultSaveConfigurationImpl config,
                                              @PathVariable("url") String url) {

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);
        // Cases like POST/GET/PUT/DELETE /drive/image/path/ (because the last "/") will be linked /drive
        if (url.startsWith("image/")) {
            response.setStatus(400);
        }

        // Processing
        FileDescription fileDescription = objectService.uploadAndReplace(meta, config, url, data, false);
        boolean result = fileDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(fileDescription);

    }

    /**
     * API 4.2.5.2 v0.1.5
     *
     * @param request
     * @param response
     * @param json
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.PUT)
    public ObjectSecureResponseDTO updateFilePureJson(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody ObjectDTOWithData json,
                                                      @PathVariable("url") String url) {
        byte[] data = json.getData() == null ? null : json.getData().getBytes();

        // Protection code
        if (json.getType() == null || (!json.getType().equals("object") && !json.getType().equals("directory"))) {
            response.setStatus(400);
            return null;
        }
        if (url.startsWith("/")) url = url.substring(1);
        // Cases like POST/GET/PUT/DELETE /drive/image/path/ (because the last "/") will be linked /drive
        if (url.startsWith("image/")) {
            response.setStatus(400);
        }

        // Processing
        FileDescription fileDescription = objectService.uploadAndReplace(json, url, data, json.getType().equals("directory"));
        boolean result = fileDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(fileDescription);

    }

    /**
     * M4.2.14 Get info of url
     *
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.GET)
    public ObjectSecureResponseDTO getUrl(HttpServletRequest request, HttpServletResponse response,
                                          @PathVariable String url) {
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        FileDescription result = objectService.getUrlDescription(url);
        if (result == null) {
            response.setStatus(404);
            return null;
        }

        return ObjectSecureResponseDTO.secureCopy(result);
    }

    /**
     * Image APIs
     */
    /**
     * API 4.2.1.2 v0.1.5
     *
     * @param request
     * @param response
     * @param meta
     * @param file
     * @param config
     * @return
     */
    @RequestMapping(value = "/drive/image", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectSecureResponseDTO uploadImage(HttpServletRequest request, HttpServletResponse response,
                                               @RequestPart("meta") ImageMetaWithTag meta,
                                               @RequestPart("file") MultipartFile file,
                                               @RequestPart("config") ImageSaveConfiguration config) {

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Processing
        ImageDescription imageDescription = objectService.uploadImage(meta, config, meta.getFilename(), data);
        boolean result = imageDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(imageDescription);

    }

    /**
     * API 4.2.1.1 v0.1.5
     *
     * @param request
     * @param response
     * @param json
     * @return
     */
    @RequestMapping(value = "/drive/image/base64", method = RequestMethod.POST)
    public ObjectSecureResponseDTO uploadImageBase64(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody ImageBase64DTO json) {

        // Protection Code
        byte[] data = Base64.getDecoder().decode(json.getData().split(",")[1].getBytes());

        // Processing
        ImageDescription imageDescription = objectService.uploadImage(json, json.getMeta().getFilename(), data);
        boolean result = imageDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(imageDescription);

    }

    /**
     * API 4.2.4.1 v0.1.5
     *
     * @param request
     * @param response
     * @param meta
     * @param file
     * @param config
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/image/{*url}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ObjectSecureResponseDTO updateImage(HttpServletRequest request, HttpServletResponse response,
                                               @RequestPart("meta") ObjectMetaWithTag meta,
                                               @RequestPart("file") MultipartFile file,
                                               @RequestPart("config") DefaultSaveConfigurationImpl config,
                                               @PathVariable("url") String url) {

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        // Processing
        ImageDescription imageDescription = objectService.updateImage(meta, config, url, data);
        boolean result = imageDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(imageDescription);

    }

    /**
     * API 4.2.4.2 v0.1.5
     *
     * @param request
     * @param response
     * @param json
     * @param url
     * @return
     */
    @RequestMapping(value = "/drive/image/base64/{*url}", method = RequestMethod.PUT)
    public ObjectSecureResponseDTO updateImageBase64(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody ImageBase64DTO json,
                                                     @PathVariable("url") String url) {

        byte[] data = Base64.getDecoder().decode(json.getData().getBytes());

        // Protection code
        if (!json.getType().equals("object") && !json.getType().equals("directory")) {
            response.setStatus(400);
            return null;
        }
        if (url.startsWith("/")) url = url.substring(1);

        // Processing

        json.setUrl("image/" + json.getUrl());
        ImageDescription imageDescription = objectService.updateImage(json, url, data);
        boolean result = imageDescription != null;
        if (!result) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return ObjectSecureResponseDTO.secureCopy(imageDescription);

    }

    /**
     * API 4.2.8 v0.1.5
     *
     * @param request
     * @param response
     * @param url
     */
    @RequestMapping(value = "/drive/image/{*url}", method = RequestMethod.DELETE)
    public void deleteImage(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("url") String url) {

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        objectService.removeImage(url);

        response.setStatus(204);

    }

    /**
     * API 4.2.9 v0.1.5
     *
     * @param request
     * @param response
     * @param url
     */
    @RequestMapping(value = "/drive/{*url}", method = RequestMethod.DELETE)
    public void deleteObject(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable("url") String url) {
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        objectService.remove(url);

        response.setStatus(204);
    }

    /**
     * API M4.2.6.1 Get image as file
     *
     * @param request
     * @param response
     * @param url
     */
    @RequestMapping(value = "/drive/image/{*url}", method = RequestMethod.GET)
    @CrossOrigin(value = "localhost:5173")
    public ResponseEntity<byte[]> getImageAsFile(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable("url") String url) {

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        ObjectRecord or = objectService.getImage(url);
        if (or == null) {
            response.setStatus(404);
            return null;
        }

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
     * API M4.2.6.2 Get image as JSON
     *
     * @param request
     * @param response
     * @param url
     */
    @RequestMapping(value = "/drive/image/base64/{*url}", method = RequestMethod.GET)
    public String getImageAsJson(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("url") String url) {

        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        ObjectRecord or = objectService.getThumb(url);

        if (or == null) {
            response.setStatus(404);
            return null;
        }

        ObjectSecureResponseDTOWithData osr
                = ObjectSecureResponseDTOWithData.secureCopy(or);


        response.setStatus(200);

        return String.format("data:%s;base64,%s", osr.getMeta().getMimeType(), osr.getData());
    }

    /**
     * API M4.2.7 Get object as file
     *
     * @param request
     * @param response
     * @param url
     */

    @RequestMapping(value = "/drive/object/{*url}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getObjectAsFile(HttpServletRequest request, HttpServletResponse response,
                                                  @PathVariable("url") String url) {
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        ObjectRecord or = objectService.getObject(url);
        if (or == null) {
            response.setStatus(404);
            return null;
        }

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
     * Memo APIs
     */

    /**
     * API M4.2.10 v0.1.5 Upload a Memo
     *
     * @param request
     * @param response
     * @param memoDTO  A helper class for deserialization
     * @param userId
     * @return
     */
    @RequestMapping(value = "drive/memo/{userId}", method = RequestMethod.POST)
    public MemoResponseDTO uploadMemo(HttpServletRequest request, HttpServletResponse response, @RequestBody MemoDTO memoDTO, @PathVariable String userId) {

        logger.info(memoDTO.toString());
        Memo memo = memoDTO.getMemo();
        Memo result = objectService.uploadMemo(memo, userId, true);
        if (result == null) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(201);
        return new MemoResponseDTO(result);

    }

    /**
     * API M4.2.11 v0.1.5 Get a Memo
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "drive/memo/{*url}", method = RequestMethod.GET)
    public MemoResponseDTO getMemo(HttpServletRequest request, HttpServletResponse response, @PathVariable String url) {
        logger.info("url: " + url);
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        Memo result = objectService.getMemo(url);
        if (result == null) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(200);
        return new MemoResponseDTO(result);
    }

    /**
     * API M4.2.12.1 v0.1.5 Update a Memo
     *
     * @param request
     * @param response
     * @param memoDTO
     * @param url
     * @return
     */
    @RequestMapping(value = "drive/memo/{*url}", method = RequestMethod.PUT)
    public MemoResponseDTO updateMemo(HttpServletRequest request, HttpServletResponse response, @RequestBody MemoDTO memoDTO, @PathVariable String url) {

        Memo memo = memoDTO.getMemo();
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        Memo result = objectService.updateMemo(url, memo, HttpMethod.PUT);
        if (result == null) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(200);
        return new MemoResponseDTO(result);
    }

    /**
     * API M4.2.12.1 v0.1.5 Update a Memo - PATCH method
     *
     * @param request
     * @param response
     * @param memoDTO
     * @param url
     * @return
     */
    @RequestMapping(value = "drive/memo/{*url}", method = RequestMethod.PATCH)
    public MemoResponseDTO patchMemo(HttpServletRequest request, HttpServletResponse response, @RequestBody MemoDTO memoDTO, @PathVariable String url) {
        Memo memo = memoDTO.getMemo();
        // Protection Code
        if (url.startsWith("/")) url = url.substring(1);

        Memo result = objectService.updateMemo(url, memo, HttpMethod.PATCH);
        if (result == null) {
            response.setStatus(400);
            return null;
        }

        response.setStatus(200);
        return new MemoResponseDTO(result);
    }
}
