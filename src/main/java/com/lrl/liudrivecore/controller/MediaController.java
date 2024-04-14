package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.pojo.*;
import com.lrl.liudrivecore.service.ImageService;
import com.lrl.liudrivecore.service.MediaFileService;
import com.lrl.liudrivecore.service.location.DefaultSaveConfiguration;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.AudioMetaJsonTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.MediaMetaTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.VideoMetaJsonTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Handle video and audio files uploading and reading.
 * Size of files should not exceed the limit of server http capacity.(5MB v0.1.3.5)
 */
@RestController
@RequestMapping("/drive")
public class MediaController {

    private Logger logger = LoggerFactory.getLogger(MediaController.class);

    private ImageService imageService;

    private MediaFileService mediaFileService;


    @Autowired
    public MediaController(MediaFileService mService, ImageService iService) {
        this.imageService = iService;
        this.mediaFileService = mService;
    }


    @RequestMapping(value = "/media", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public MediaFileMeta postMediaFile(HttpServletRequest request, HttpServletResponse response,
                                       @RequestPart("meta") MediaMetaTemplate template,
                                       @RequestPart("file") MultipartFile file,
                                       @RequestPart("config")DefaultSaveConfiguration configuration) {

        String mimeType = template.getType();
        MediaFileMeta result = null;

        try {
            if (file == null || file.getBytes().length == 0) {
                response.setStatus(400);
                return null;
            }
            if (isImageType(mimeType)) {
                result = imageService.upload(template.asImageMeta(), file.getBytes(), configuration);
            } else if (isVideoType(mimeType)) {
                mediaFileService.uploadVideo(template.asVideoMeta(), file.getBytes(), configuration);
            } else if (isAudioType(mimeType)) {
                mediaFileService.uploadAudio(template.asAudioMeta(), file.getBytes(), configuration);
            } else throw new RuntimeException("Wrong MIME type uploading with MediaController");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (result == null) {
            response.setStatus(400);
            return null;

        } else {
            response.setStatus(204);
            return result;
        }

    }


    @RequestMapping(value = "/gallery/{userId}/page/{page}", method = RequestMethod.GET)
    public List<CustomizedGalleryDataImpl> getImageMetaListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                                    @PathVariable String userId,
                                                                    @PathVariable Integer page) {
        logger.info("Getting user Gallery list of page" + page);

        return mediaFileService.getGalleryList(userId, page);
    }

    @RequestMapping(value = "/video/{userId}/page/{page}", method = RequestMethod.GET)
    public List<VideoMetaJsonTemplate> getVideoListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                            @PathVariable String userId,
                                                            @PathVariable Integer page) {
        logger.info("Getting user: " + userId + " video list of page: " + page);

        return mediaFileService.getVideoList(userId, page);
    }

    @RequestMapping(value = "/audio/{userId}/page/{page}", method = RequestMethod.GET)
    public List<AudioMetaJsonTemplate> getAudioListOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                            @PathVariable String userId,
                                                            @PathVariable Integer page) {
        logger.info("Getting user: " + userId + " audio list of page: " + page);

        return mediaFileService.getAudioList(userId, page);
    }

    @RequestMapping(value = "/video/{userId}/{subUrl}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getVideoOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable String userId, @PathVariable String subUrl) throws IOException {
        Resource resource = mediaFileService.getVideo(userId, subUrl);
        System.out.println("Debug: userId: "+userId);
        System.out.println("Debug: subUrl: "+subUrl);
        System.out.println("Debug: pathUrl: "+ userId + File.separator + subUrl);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" +  URLEncoder.encode(resource.getFilename(), "UTF-8"))
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    @RequestMapping(value = "/video/{userId}/{subUrl}/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadVideoOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable String userId, @PathVariable String subUrl) throws IOException {
        Resource resource = mediaFileService.getVideo(userId, userId + File.separator + subUrl);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    @RequestMapping(value = "/video/{userId}/{subUrl}", method = RequestMethod.DELETE)
    public void deleteVideoOfUserId(HttpServletRequest request, HttpServletResponse response,
                                                     @PathVariable String userId, @PathVariable String subUrl) throws IOException {

        if(!mediaFileService.isOwnerOfVideo(userId, userId+"/"+subUrl)){
            response.setStatus(403);
        }

        if(!mediaFileService.deleteVideo(userId + "/" + subUrl)){
            response.setStatus(400);
        }

    }


    private boolean isImageType(String mimeType) {
        MimeType m = new MimeType(mimeType);
        return m.getType().equals(MediaType.IMAGE_JPEG.getType());
    }

    private boolean isVideoType(String mimeType) {
        MimeType m = new MimeType(mimeType);
        return m.getType().equals(new MimeType("video").getType());
    }

    private boolean isAudioType(String mimeType) {
        MimeType m = new MimeType(mimeType);
        return m.getType().equals(new MimeType("audio").getType());
    }


}
