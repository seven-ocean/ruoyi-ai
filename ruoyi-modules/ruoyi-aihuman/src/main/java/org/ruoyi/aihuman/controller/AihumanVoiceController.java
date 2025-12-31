package org.ruoyi.aihuman.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SaIgnore
@Validated
@RequiredArgsConstructor
@RestController
public class AihumanVoiceController {

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

    @GetMapping("/voice/{fileName:.+}")
    public ResponseEntity<byte[]> serveVoice(@PathVariable String fileName, @RequestHeader(value = "Origin", required = false) String origin) {
        try {
            Path p = Paths.get(getVoiceDirectoryPath(), fileName);
            if (!Files.exists(p)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            byte[] data = Files.readAllBytes(p);
            HttpHeaders headers = new HttpHeaders();
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".wav")) headers.setContentType(MediaType.parseMediaType("audio/wav"));
            else if (lower.endsWith(".mp3")) headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            else if (lower.endsWith(".ogg")) headers.setContentType(MediaType.parseMediaType("audio/ogg"));
            else headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(data.length);
            headers.add("Accept-Ranges", "bytes");
            headers.add("Access-Control-Allow-Origin", origin != null ? origin : "*");
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

