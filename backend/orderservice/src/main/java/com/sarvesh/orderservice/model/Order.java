package com.sarvesh.orderservice.model;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Represents an Order stored in the DynamoDB "orders" table.
 */
@DynamoDBTable(tableName = "Orders")
public class Order {

    private String orderId;
    private String customerName;
    private Double amount;
    private String invoiceUrl;
    private Date createdAt;

    /**
     * Gets the primary key for the Order item in DynamoDB.
     * 
     * @return the order ID
     */
    @DynamoDBHashKey(attributeName = "orderId")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the name of the customer who placed the order.
     * 
     * @return the customer's name
     */
    @DynamoDBAttribute(attributeName = "customerName")
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the total amount for the order.
     * 
     * @return the order amount
     */
    @DynamoDBAttribute(attributeName = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Gets the URL pointing to the invoice (likely stored in S3).
     * 
     * @return the invoice URL
     */
    @DynamoDBAttribute(attributeName = "invoiceUrl")
    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    /**
     * Gets the timestamp indicating when the order was created.
     * 
     * @return the creation date
     */
    @DynamoDBAttribute(attributeName = "createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns a string representation of the order.
     */
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