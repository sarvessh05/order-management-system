package com.sarvesh.orderservice.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.sarvesh.orderservice.model.Order;

@EnableScan
public interface OrderRepository extends CrudRepository<Order, String> {
    // Custom queries can go here
}