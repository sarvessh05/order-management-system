package com.sarvesh.orderservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import java.util.Date;

@DynamoDBTable(tableName = "orders")
public class Order {

    private String orderId;
    private String customerName;
    private Double amount;
    private String invoiceUrl;
    private Date createdAt;

    @DynamoDBHashKey(attributeName = "orderId")
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @DynamoDBAttribute(attributeName = "customerName")
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @DynamoDBAttribute(attributeName = "amount")
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @DynamoDBAttribute(attributeName = "invoiceUrl")
    public String getInvoiceUrl() {
        return invoiceUrl;
    }
    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    @DynamoDBAttribute(attributeName = "createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}