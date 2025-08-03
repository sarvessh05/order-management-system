package com.sarvesh.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.service.OrderService;
import com.sarvesh.orderservice.service.S3Service;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final S3Service s3Service;

    @Autowired
    public OrderController(OrderService orderService, S3Service s3Service) {
        this.orderService = orderService;
        this.s3Service = s3Service;
    }

    /**
     * Create a new order using plain JSON (no file upload)
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            logger.info("Received new order request: {}", order);
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            logger.error("Error while creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new order with optional invoice file upload.
     * Endpoint: POST /orders/upload
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Order> createOrderWithInvoice(
            @RequestPart("order") Order order,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            // Handle optional file upload
            if (file != null && !file.isEmpty()) {
                String s3Key = s3Service.uploadFile(file);
                String invoiceUrl = "https://s3.localhost.localstack.cloud:4566/my-local-bucket/" + s3Key;
                order.setInvoiceUrl(invoiceUrl);
                logger.info("Invoice uploaded. S3 Key: {}, URL: {}", s3Key, invoiceUrl);
            }

            // Save order to DynamoDB
            Order savedOrder = orderService.createOrder(order);
            logger.info("Order created successfully: {}", savedOrder.getOrderId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

        } catch (Exception e) {
            logger.error("Error while creating order with invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get a single order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        logger.info("Fetching order with ID: {}", orderId);
        return orderService.getOrderById(orderId)
                .map(order -> ResponseEntity.ok().body(order))
                .orElseGet(() -> {
                    logger.warn("Order with ID {} not found", orderId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    /**
     * Get all orders
     */
    @GetMapping
    public ResponseEntity<Iterable<Order>> getAllOrders() {
        try {
            logger.info("Fetching all orders");
            Iterable<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error while fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test endpoint to verify S3 bucket integration
     */
    @GetMapping("/test-s3")
    public ResponseEntity<String> testS3Integration() {
        String bucketName = "my-test-bucket";
        try {
            logger.info("Testing S3 bucket: {}", bucketName);
            s3Service.createBucketIfNotExists(bucketName);
            return ResponseEntity.ok("S3 bucket '" + bucketName + "' is ready!");
        } catch (Exception e) {
            logger.error("S3 integration test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("S3 bucket test failed: " + e.getMessage());
        }
    }
}