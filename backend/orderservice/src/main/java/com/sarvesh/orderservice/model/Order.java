package com.sarvesh.orderservice.model;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Represents an order entry stored in the DynamoDB "Orders" table.
 */
@DynamoDBTable(tableName = "Orders")
public class Order {

    private String orderId;
    private String customerName;
    private Double amount;
    private String invoiceUrl;  // URL or S3 key of the uploaded invoice
    private Date createdAt;

    /** 
     * Primary key for the order.
     */
    @DynamoDBHashKey(attributeName = "orderId")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /** 
     * Customer name associated with the order.
     */
    @DynamoDBAttribute(attributeName = "customerName")
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** 
     * Amount paid for the order.
     */
    @DynamoDBAttribute(attributeName = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * URL or identifier pointing to the invoice stored in S3.
     */
    @DynamoDBAttribute(attributeName = "invoiceUrl")
    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    /** 
     * Timestamp when the order was created.
     */
    @DynamoDBAttribute(attributeName = "createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Order{" +
               "orderId='" + orderId + '\'' +
               ", customerName='" + customerName + '\'' +
               ", amount=" + amount +
               ", invoiceUrl='" + invoiceUrl + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}