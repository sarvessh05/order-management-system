package com.sarvesh.orderservice.controller;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create a new order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            System.out.println("Received Order: " + order); // Debug log
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            e.printStackTrace(); // Log full stack trace in terminal
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create order: " + e.getMessage());
        }
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<Iterable<Order>> getAllOrders() {
        try {
            Iterable<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}