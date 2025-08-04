package com.sarvesh.orderservice.service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.repository.OrderRepository;

/**
 * Service class responsible for handling business logic related to Orders.
 * This includes optional invoice upload to S3 and sending SNS notifications.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final S3Service s3Service;
    private final SnsService snsService;

    /**
     * Constructor-based dependency injection for repository and AWS-related services.
     */
    public OrderService(OrderRepository orderRepository, S3Service s3Service, SnsService snsService) {
        this.orderRepository = orderRepository;
        this.s3Service = s3Service;
        this.snsService = snsService;
    }

    /**
     * Creates a new order with optional invoice upload.
     * 
     * Process:
     * 1. Generate UUID for the order.
     * 2. If file is provided, upload it to S3 and save the file URL.
     * 3. Save the order in DynamoDB.
     * 4. Publish an SNS event indicating the new order.
     *
     * @param order The order details from client
     * @param file Optional invoice file
     * @return The saved Order object
     * @throws IOException If S3 upload fails
     */
    public Order createOrder(Order order, MultipartFile file) throws IOException {
        // Step 1: Assign unique order ID
        String generatedOrderId = UUID.randomUUID().toString();
        order.setOrderId(generatedOrderId);

        // Step 2: Upload file to S3 if present
        if (file != null && !file.isEmpty()) {
            String invoiceUrl = s3Service.uploadFile(file);
            order.setInvoiceUrl(invoiceUrl);
        }

        // Step 3: Save the order in DynamoDB
        Order savedOrder = orderRepository.save(order);

        // Step 4: Notify via SNS
        snsService.publishOrderEvent("🆕 Order Created: " + savedOrder.getOrderId());

        return savedOrder;
    }

    /**
     * Retrieve a specific order by its ID.
     *
     * @param orderId The unique identifier of the order
     * @return Optional containing the order if it exists
     */
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Fetch all orders from the database.
     * Note: Uses full table scan; optimize in production.
     *
     * @return All stored orders
     */
    public Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}