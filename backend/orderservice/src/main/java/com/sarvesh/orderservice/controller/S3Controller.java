package com.sarvesh.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sarvesh.orderservice.service.S3Service;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String key = s3Service.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully! S3 Key: " + key);
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}