package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.data.pojo.mongo.FileDescription;
import com.lrl.liudrivecore.data.repo.FileDescriptionRepository;
import com.lrl.liudrivecore.service.ObjectService;
import com.lrl.liudrivecore.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
public class TestController {

    @Autowired
    UserAuthService service;

    @Autowired
    FileDescriptionRepository userDirectoryRepository;

    @Autowired
    ObjectService objectService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {

        System.out.println("Reached");

    }

    @RequestMapping(value = "/test/getFile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFile(HttpServletResponse response) {

        String filePath = "E:\\Workspace\\liu-station\\artifact\\liu-station-backend\\apiTestRepository\\web-reactive.pdf"; // 修改为实际文件的路径

        // 读取文件内容
        File file = new File(filePath);
        FileInputStream inputStream;
        byte[] testFileContent;
        try {
            inputStream = new FileInputStream(file);
            testFileContent = inputStream.readAllBytes();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 设置响应头，指示文件下载
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file.txt"); // 设置下载时的文件名

        // 构建响应实体
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(testFileContent, headers, org.springframework.http.HttpStatus.OK);
        return responseEntity;
    }
//    @RequestMapping(value = "/test/multipart", method = RequestMethod.POST)
//    public void getMultipart(@RequestBody TestFormHolder holder){
//
//        System.out.println(holder.getMeta());
//
////        System.out.println(mood);
//
//    }

    @RequestMapping(value = "/test/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getTestVideo(HttpServletResponse response) throws IOException {

//        Resource video = new ClassPathResource("test.mp4");
//        byte[] data = video.getContentAsByteArray();
        File f = new File("C:\\Users\\liuru\\Videos\\Captures\\test.mp4");
        FileInputStream input = new FileInputStream(f);
//        return ResponseEntity.ok().contentLength(video.contentLength()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(video);
        response.getOutputStream().write(input.readAllBytes());
        response.getOutputStream().flush();
    }

    @RequestMapping(value = "/test/urlTest/{url}", headers = "Test=ttaa", method = RequestMethod.GET)
    public void urlReadTest(HttpServletRequest request, HttpServletResponse response, @PathVariable String url){
        System.out.println("a");
        System.out.println(url);
    }

    @RequestMapping(value = "/test/urlTest/{url}", headers = "Test=ttbb", method = RequestMethod.GET)
    public void urlReadTest2(HttpServletRequest request, HttpServletResponse response, @PathVariable String url){
        System.out.println("b");
        System.out.println(url);
    }

    @RequestMapping(value = "/test/video/{userId}/{subUrl}", method = RequestMethod.GET)
    public ResponseEntity<Resource> urlReadVideo(HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable String userId, @PathVariable String subUrl) throws IOException {
        File f = new File("C:\\Users\\liuru\\Videos\\Captures\\原神 2022-09-29 11-49-35.mp4");
        Resource resource = new FileSystemResource(f);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + f.getName())
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }


    @RequestMapping(value = "/test/dir", method = RequestMethod.GET)
    public void testDir(HttpServletRequest request, HttpServletResponse response, @RequestBody FileDescription directory
                        ,@Value("${drive.root}")String v) throws IOException {

        System.out.println(directory.toString());
        System.out.println(v);

    }
    @RequestMapping(value = "/test/{*url}", method = RequestMethod.POST)
    public void testDir(HttpServletRequest request, HttpServletResponse response, @RequestBody ObjectDTO objectDTO, @PathVariable String url
                        ) throws IOException {

        System.out.println(objectDTO.toString());
        if(url.startsWith("/")) url = url.substring(1);
        boolean res = objectService.upload(objectDTO, url, "testcontent".getBytes()) == null;

        System.out.println(res);

    }


    @RequestMapping(value = "/test/header", method = RequestMethod.GET)
    public void testHeader(HttpServletRequest request, HttpServletResponse response
                        ) throws IOException {

        System.out.println(request.getHeader("Authorization"));

    }


}
