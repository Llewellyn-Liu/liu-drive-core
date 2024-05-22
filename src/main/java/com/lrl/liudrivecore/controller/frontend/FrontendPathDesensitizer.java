package com.lrl.liudrivecore.controller.frontend;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrontendPathDesensitizer {

    @RequestMapping(value = "/page/**")
    public ResponseEntity<Resource> pathHandlerForSlashPage(){
        Resource resource = new ClassPathResource("/static/index.html");

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @RequestMapping(value = "/app/**")
    public ResponseEntity<Resource> pathHandlerForSlashApp(){
        Resource resource = new ClassPathResource("/static/index.html");

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }
}
