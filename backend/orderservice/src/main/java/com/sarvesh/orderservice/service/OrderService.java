package com.sarvesh.orderservice.service;

import com.sarvesh.orderservice.model.Order;
import com.sarvesh.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        System.out.println("Saving Order: " + order); // Debug log
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Iterable<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}