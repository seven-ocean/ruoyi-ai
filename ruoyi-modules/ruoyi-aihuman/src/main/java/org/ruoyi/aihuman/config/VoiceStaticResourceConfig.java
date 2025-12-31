package org.ruoyi.aihuman.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class VoiceStaticResourceConfig implements WebMvcConfigurer {

    private String getVoiceDirectoryPath() {
        try {
            String projectRoot = System.getProperty("user.dir");
            File targetDir = new File(projectRoot, "ruoyi-modules/ruoyi-aihuman/src/main/resources/voice");
            if (!targetDir.exists()) {
                boolean created = targetDir.mkdirs();
                if (!created) {
                    File fallbackDir = new File(projectRoot, "voice");
                    if (!fallbackDir.exists()) {
                        fallbackDir.mkdirs();
                    }
                    return fallbackDir.getAbsolutePath();
                }
            }
            return targetDir.getAbsolutePath();
        } catch (Exception e) {
            File safeDir = new File("voice");
            if (!safeDir.exists()) {
                safeDir.mkdirs();
            }
            return safeDir.getAbsolutePath();
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String voicePath = getVoiceDirectoryPath();
        registry.addResourceHandler("/voice/**")
                .addResourceLocations("file:" + voicePath + File.separator)
                .setCachePeriod(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/voice/**")
                .allowedOrigins("http://localhost:5666", "*")
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowCredentials(false);
    }
}
