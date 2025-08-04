package com.sarvesh.orderservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.net.URI;

@Service
@Slf4j
public class SnsService {

    @Value("${aws.sns.endpoint}")
    private String snsEndpoint;

    @Value("${aws.sns.region}")
    private String region;

    @Value("${aws.sns.topicArn}")
    private String topicArn;

    private SnsClient snsClient;

    @PostConstruct
    public void init() {
        this.snsClient = SnsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .endpointOverride(URI.create(snsEndpoint))
                .region(Region.of(region))
                .build();

        log.info("✅ SNS Client initialized with endpoint: {}", snsEndpoint);
    }

    public void publishOrderEvent(String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("📣 Published to SNS. MessageId: {}", response.messageId());
        } catch (Exception e) {
            log.error("❌ Failed to publish to SNS", e);
        }
    }
}