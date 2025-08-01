package com.sarvesh.orderservice.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.sarvesh.orderservice.model.Order;

/**
 * Repository interface for performing CRUD operations on Order entities stored in DynamoDB.
 *
 * Notes:
 * - @EnableScan is required for findAll(), but avoid it for large-scale datasets.
 * - For optimal performance, prefer key-based queries or use Global Secondary Indexes (GSIs).
 */
@EnableScan
public interface OrderRepository extends CrudRepository<Order, String> {

    /**
     * Example custom query based on customer name.
     * Ensure 'customerName' is indexed in your DynamoDB table to use this efficiently.
     */
    // List<Order> findByCustomerName(String customerName);

}