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
    private String invoiceUrl;
    private Date createdAt;

    /**
     * Gets the unique identifier for the order.
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
     * @return customer name
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
     * @return order amount
     */
    @DynamoDBAttribute(attributeName = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Gets the URL pointing to the uploaded invoice (typically stored in S3).
     *
     * @return invoice URL
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
     * @return creation date
     */
    @DynamoDBAttribute(attributeName = "createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns a readable string representation of the order.
     *
     * @return order details
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