package com.sarvesh.orderservice.repository;

import com.sarvesh.orderservice.model.Order;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface OrderRepository extends CrudRepository<Order, String> {
    // You can add: List<Order> findByCustomerName(String name); later if needed
}