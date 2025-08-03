package com.sarvesh.orderservice.service;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private S3Client s3Client;

    // Inject region, endpoint, and bucket name from application properties
    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Initializes the S3 client after the service is constructed.
     * Connects to the LocalStack S3 endpoint with dummy credentials.
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing S3 client for region '{}' and endpoint '{}'", region, endpoint);

            // Build the S3 client pointing to LocalStack
            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint)) // LocalStack URI (e.g. http://localhost:4566)
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create("test", "test") // dummy creds for LocalStack
                            )
                    )
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true) // Required for LocalStack
                                    .build()
                    )
                    .build();

            // Create the bucket if it doesn't already exist
            createBucketIfNotExists(bucketName);

        } catch (Exception e) {
            logger.error("Failed to initialize S3 client: {}", e.getMessage(), e);
            throw new RuntimeException("S3 client initialization failed", e);
        }
    }

    /**
     * Checks if the specified bucket exists, and creates it if not.
     */
    public void createBucketIfNotExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            logger.debug("Bucket '{}' already exists", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                logger.warn("Bucket '{}' not found. Creating new one...", bucketName);
                try {
                    s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                    logger.info("Bucket '{}' created successfully.", bucketName);
                } catch (S3Exception createEx) {
                    logger.error("Failed to create bucket '{}': {}", bucketName, createEx.awsErrorDetails().errorMessage(), createEx);
                    throw createEx;
                }
            } else {
                logger.error("Error checking bucket '{}': {}", bucketName, e.awsErrorDetails().errorMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Uploads the given file to S3 under a unique key.
     *
     * @param file MultipartFile to be uploaded
     * @return Generated key used for the file in S3
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Validate input
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("File must have a valid name");
        }

        // Create a unique S3 key using UUID
        String key = UUID.randomUUID() + "_" + originalFilename;

        // Prepare the PutObject request
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            // Upload to S3
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            logger.info("File '{}' uploaded successfully to bucket '{}' with key '{}'", originalFilename, bucketName, key);
        } catch (S3Exception e) {
            logger.error("Failed to upload file '{}' to bucket '{}': {}", originalFilename, bucketName, e.awsErrorDetails().errorMessage(), e);
            throw new IOException("S3 upload failed", e);
        }

        return key;
    }
}