package com.lrl.liudrivecore.service;

import com.lrl.liudrivecore.data.pojo.*;
import com.lrl.liudrivecore.data.repo.AudioMetaRepository;
import com.lrl.liudrivecore.data.repo.ImageMetaRepository;
import com.lrl.liudrivecore.data.repo.VideoMetaRepository;
import com.lrl.liudrivecore.service.tool.intf.*;
import com.lrl.liudrivecore.service.tool.stereotype.PathStereotype;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.AudioMetaJsonTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.VideoMetaJsonTemplate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MediaFileService {

    private static Logger logger = LoggerFactory.getLogger(MediaFileService.class);

    private static final int PAGE_SIZE = 100;

    private ImageService imageService;

    private VideoMetaRepository videoMetaRepository;

    private VideoSaver videoSaver;

    private VideoReader videoReader;

    private AudioMetaRepository audioMetaRepository;

    private AudioSaver audioSaver;

    private AudioReader audioReader;

    private ImageMetaRepository imageMetaRepository;

    @Autowired
    public MediaFileService(VideoMetaRepository videoMetaRepository,
                            AudioMetaRepository audioMetaRepository,
                            ImageMetaRepository imageMetaRepository,
                            AudioSaver audioSaver,
                            AudioReader audioReader,
                            VideoSaver videoSaver,
                            VideoReader videoReader,
                            ImageService imageService) {
        this.videoReader = videoReader;
        this.videoSaver = videoSaver;
        this.audioReader = audioReader;
        this.audioSaver = audioSaver;
        this.videoMetaRepository = videoMetaRepository;
        this.audioMetaRepository = audioMetaRepository;
        this.imageMetaRepository = imageMetaRepository;
        this.imageService = imageService;
    }


    @Transactional
    public boolean uploadVideo(VideoMeta meta, byte[] data) {

        logger.info("VideoService upload: " + meta);

        // load additional attrs
        PathStereotype.buildUrl(meta);
        meta.setDateCreated(ZonedDateTime.now());

        // Save Meta
        saveVideoMeta(meta);

        // Save Data
        saveVideoFileData(meta.getUrl(), data);

        return true;
    }

    @Transactional
    public boolean deleteVideo(String pathUrl) {

        VideoMeta vm = videoMetaRepository.getByUrl(pathUrl);
        if(vm == null) return false;
        videoMetaRepository.delete(vm);

        boolean r = videoSaver.delete(pathUrl);

        return r;
    }

    @Transactional
    public boolean uploadAudio(AudioMeta meta, byte[] data) {

        logger.info("VideoService upload: " + meta);

        // load additional attrs
        PathStereotype.buildUrl(meta);
        meta.setDateCreated(ZonedDateTime.now());

        // Save Meta
        saveAudioMeta(meta);

        // Save Data
        saveAudioFileData(meta.getUrl(), data);

        return true;
    }

    public boolean deleteAudio(String pathUrl) {
        return audioSaver.delete(pathUrl);
    }

    public List<CustomizedGalleryDataImpl> getGalleryList(String userId, Integer page) {

        List<CustomizedGalleryData> list = imageMetaRepository.getJointQueryFromImageAndVideo(userId,
                PageRequest.of(page, PAGE_SIZE));

        List<CustomizedGalleryDataImpl> rev = packageCustomizedQueryResult(list);

        return rev;

    }

    public List<VideoMetaJsonTemplate> getVideoList(String userId, Integer page) {
        List<VideoMeta> list = videoMetaRepository.findAllByUserId(userId, PageRequest.of(page, PAGE_SIZE));
        ArrayList<VideoMetaJsonTemplate> rev = new ArrayList<>();
        for (VideoMeta vm : list) {
            rev.add(new VideoMetaJsonTemplate(vm));
        }
        return rev;
    }

    public List<AudioMetaJsonTemplate> getAudioList(String userId, Integer page) {
        List<AudioMeta> list = audioMetaRepository.findAllByUserId(userId, PageRequest.of(page, PAGE_SIZE));
        ArrayList<AudioMetaJsonTemplate> rev = new ArrayList<>();
        for (AudioMeta am : list) {
            rev.add(new AudioMetaJsonTemplate(am));
        }
        return rev;
    }

    public Resource getVideo(String userId, String pathUrl){

        VideoMeta m = videoMetaRepository.getByUrl(pathUrl);
        if(m == null || !m.getUserId().equals(userId))
            return null;

        Resource r = videoReader.getFileAsResource(pathUrl);
        return r;

    }

    private List<CustomizedGalleryDataImpl> packageCustomizedQueryResult(List<CustomizedGalleryData> list) {
        ArrayList<CustomizedGalleryDataImpl> packagedList = new ArrayList<>();
        for (CustomizedGalleryData d : list) {
            CustomizedGalleryDataImpl p = new CustomizedGalleryDataImpl(
                    d.getFilename(),
                    d.getUrl(),
                    d.getUserId(),
                    d.getTags(),
                    d.getAuthor(),
                    d.getDateCreated(),
                    d.getAccessibility(),
                    d.getType(),
                    d.getScale()
            );
            packagedList.add(p);
        }
        return packagedList;
    }

    private void saveVideoFileData(String pathUrl, byte[] data) {

        videoSaver.save(pathUrl, data);

    }

    private void saveVideoMeta(VideoMeta meta) {
        if (videoMetaRepository.getByUrl(meta.getUrl()) != null) {
            throw new RuntimeException("Video url conflicts with existing resource");
        }
        videoMetaRepository.save(meta);
    }


    private void saveAudioFileData(String pathUrl, byte[] data) {

        audioSaver.save(pathUrl, data);

    }

    private void saveAudioMeta(AudioMeta meta) {
        if (audioMetaRepository.getByUrl(meta.getUrl()) != null) {
            throw new RuntimeException("Audio url conflicts with existing resource");
        }
        audioMetaRepository.save(meta);
    }

    public boolean isOwnerOfVideo(String userId, String pathUrl) {

        VideoMeta m = videoMetaRepository.getByUrl(pathUrl);
        return m.getUserId().equals(userId);
    }
}
