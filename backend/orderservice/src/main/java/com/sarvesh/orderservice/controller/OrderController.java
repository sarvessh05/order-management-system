package com.sarvesh.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.service.OrderService;
import com.sarvesh.orderservice.service.S3Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderController(OrderService orderService, S3Service s3Service) {
        this.orderService = orderService;
        this.s3Service = s3Service;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * ✅ Create a new order from raw JSON (no file upload)
     *
     * POST /orders
     * Body: JSON Order object
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        logger.info("Received createOrder request: {}", order);
        try {
            // Pass null for file since this endpoint does not support file upload
            Order savedOrder = orderService.createOrder(order, null);
            logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            logger.error("Failed to create order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * ✅ Create an order with optional invoice file (multipart/form-data)
     *
     * POST /orders/upload
     * Parts:
     * - order: JSON string
     * - file: Multipart file (optional)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createOrderWithInvoice(
            @RequestPart("order") String orderJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        logger.info("Received createOrderWithInvoice request");

        try {
            // Convert JSON string to Order object
            Order order = objectMapper.readValue(orderJson, Order.class);
            logger.debug("Parsed order JSON successfully: {}", order);

            // Upload file and set invoice URL inside service layer
            Order savedOrder = orderService.createOrder(order, file);
            logger.info("Order with invoice created. ID: {}", savedOrder.getOrderId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Invalid order JSON received", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid order JSON: " + e.getOriginalMessage());

        } catch (Exception e) {
            logger.error("Failed to create order with invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * ✅ Retrieve an order by its ID
     *
     * GET /orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        logger.info("Fetching order with ID: {}", orderId);
        try {
            return orderService.getOrderById(orderId)
                    .map(order -> {
                        logger.debug("Order found: {}", order);
                        return ResponseEntity.ok(order);
                    })
                    .orElseGet(() -> {
                        logger.warn("Order ID {} not found", orderId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            logger.error("Error while retrieving order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ✅ Retrieve all orders (⚠️ May be slow for large datasets)
     *
     * GET /orders
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        logger.info("Fetching all orders");
        try {
            Iterable<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error while fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve orders");
        }
    }

    /**
     * ✅ Endpoint to test S3 integration and bucket creation
     *
     * GET /orders/test-s3
     */
    @GetMapping("/test-s3")
    public ResponseEntity<?> testS3Integration() {
        String bucketName = "my-test-bucket";
        try {
            logger.info("Testing S3 bucket access for: {}", bucketName);
            s3Service.createBucketIfNotExists(bucketName);
            return ResponseEntity.ok("S3 bucket '" + bucketName + "' is ready!");
        } catch (Exception e) {
            logger.error("S3 bucket test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("S3 test failed: " + e.getMessage());
        }
    }
}