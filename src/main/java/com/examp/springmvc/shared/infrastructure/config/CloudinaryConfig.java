package com.examp.springmvc.shared.infrastructure.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(Environment env) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", env.getProperty("cloudinary.cloud-name"),
                "api_key", env.getProperty("cloudinary.api-key"),
                "api_secret", env.getProperty("cloudinary.api-secret")));
    }
}
