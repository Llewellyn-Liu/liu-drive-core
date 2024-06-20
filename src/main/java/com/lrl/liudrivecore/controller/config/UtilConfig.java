package com.lrl.liudrivecore.controller.config;

import com.lrl.liudrivecore.data.dto.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * For flexible usage of tools, UtilConfig enables customized settings of tools;
 */
@Configuration
public class UtilConfig {



    @Bean
    public ImageSchemaBuilder getImageSchemaBuilder(){
        return new DefaultImageSchemaBuilder();
    }

    @Bean
    public ObjectSchemaBuilder getObjectSchemaBuilder(){
        return new DefaultObjectSchemaBuilder();
    }

    @Bean
    public ImageSchemaValidator getImageSchemaValidator(){
        return new DefaultImageSchemaValidator();
    }

    @Bean
    public ObjectSchemaValidator getObjectSchemaValidator(){
        return new DefaultObjectSchemaValidator();
    }

}
