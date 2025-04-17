package com.optitop.optitop_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String[] origins;

        if ("dev".equals(activeProfile)) {
            origins = new String[] {
                    "http://localhost",
                    "http://localhost:3000",
                    "http://127.0.0.1:3000"
            };
        } else {
            origins = new String[] { "http://192.168.1.150" };
        }

        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
