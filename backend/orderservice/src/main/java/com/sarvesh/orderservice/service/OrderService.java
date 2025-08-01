package com.sarvesh.orderservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.repository.OrderRepository;

/**
 * Service layer for handling business logic related to Order operations.
 * Delegates data persistence to OrderRepository.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Constructor-based injection (recommended over field injection for testability).
     */
    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Persists a new order into the DynamoDB table.
     *
     * @param order the order object to be saved
     * @return the saved order with generated ID and timestamps
     */
    public Order createOrder(Order order) {
        // TODO: Replace with proper logging (e.g., SLF4J) if needed
        System.out.println("Creating Order: " + order);
        return orderRepository.save(order);
    }

    /**
     * Fetches an order by its unique ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return an Optional containing the order if found, or empty otherwise
     */
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Retrieves all orders from the table.
     * Note: Uses scan operation, which can be expensive on large datasets.
     *
     * @return Iterable containing all orders
     */
    public Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}