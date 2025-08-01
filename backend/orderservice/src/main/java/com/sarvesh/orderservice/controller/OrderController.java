package com.sarvesh.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.service.OrderService;
import com.sarvesh.orderservice.service.S3Service;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final S3Service s3Service;

    @Autowired
    public OrderController(OrderService orderService, S3Service s3Service) {
        this.orderService = orderService;
        this.s3Service = s3Service;
    }

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            System.out.println("Received Order: " + order);
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Retrieve order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieve all orders
     */
    @GetMapping
    public ResponseEntity<Iterable<Order>> getAllOrders() {
        try {
            Iterable<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test S3 bucket integration
     */
    @GetMapping("/test-s3")
    public ResponseEntity<String> testS3Integration() {
        try {
            String bucketName = "my-test-bucket";
            s3Service.createBucketIfNotExists(bucketName);
            return ResponseEntity.ok("S3 bucket '" + bucketName + "' is ready!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("S3 bucket test failed: " + e.getMessage());
        }
    }
}