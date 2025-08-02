package com.sarvesh.orderservice;

import com.sarvesh.orderservice.service.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootTest(
    properties = {
        "aws.s3.endpoint=http://localhost:4566",
        "aws.dynamodb.endpoint=http://localhost:4566",
        "amazon.dynamodb.endpoint=http://localhost:4566",
        "cloud.aws.region.static=us-east-1",
        "cloud.aws.stack.auto=false",
        "aws.s3.bucket=test-bucket"
    },
    args = "--add-opens=java.base/java.lang=ALL-UNNAMED"
)
class OrderserviceApplicationTests {

    // ✅ Prevent actual S3 interactions during test context loading
    @MockBean
    private S3Service s3Service;

    // ✅ Prevent actual DynamoDB client from being instantiated and calling LocalStack/AWS
    @MockBean
    private DynamoDbClient dynamoDbClient;

    @Test
    void contextLoads() {
        // ✅ If this test passes, the Spring context loads successfully with mocks
    }
}