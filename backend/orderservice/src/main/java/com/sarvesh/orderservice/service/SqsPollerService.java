package com.sarvesh.orderservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Service
public class SqsPollerService {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public SqsPollerService(SqsClient sqsClient, @Value("${aws.sqs.queueUrl}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    @Scheduled(fixedDelay = 10000)
    public void pollMessages() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .waitTimeSeconds(20)
            .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(request);
        for (Message msg : response.messages()) {
            System.out.println("Received SQS message: " + msg.body());
            // Delete after processing to prevent duplicates
            DeleteMessageRequest del = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
                .build();
            sqsClient.deleteMessage(del);
        }
    }
}