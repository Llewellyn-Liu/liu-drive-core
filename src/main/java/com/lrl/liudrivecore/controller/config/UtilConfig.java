package com.lrl.liudrivecore.controller.config;

import com.lrl.liudrivecore.service.dir.url.URLValidator;
import com.lrl.liudrivecore.service.dir.url.URLValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * For flexible usage of tools, UtilConfig enables customized settings of tools;
 */
@Configuration
public class UtilConfig {


    @Bean
    public URLValidator getUrlValidator(){
        return new URLValidatorImpl();
    }
}
