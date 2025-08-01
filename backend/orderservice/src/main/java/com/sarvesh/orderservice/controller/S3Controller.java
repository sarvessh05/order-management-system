package com.sarvesh.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    /**
     * Upload a file to the configured S3 bucket.
     *
     * @param file the multipart file to upload
     * @return ResponseEntity with upload status and S3 key
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Upload request received for file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Upload attempt with empty file.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed: File is empty.");
        }

        try {
            String key = s3Service.uploadFile(file);
            logger.info("File uploaded successfully. S3 Key: {}", key);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("File uploaded successfully! S3 Key: " + key);
        } catch (Exception e) {
            logger.error("Error uploading file to S3", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}