package com.shopsphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long paymentId;
    private Long orderId;
    private String paymentMethod;
    private String transactionId;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentGatewayResponse;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment() {
    }

    public Payment(Long paymentId, Long orderId, String paymentMethod, String transactionId,
                   BigDecimal amount, String paymentStatus, String paymentGatewayResponse,
                   LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentGatewayResponse = paymentGatewayResponse;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentGatewayResponse() {
        return paymentGatewayResponse;
    }

    public void setPaymentGatewayResponse(String paymentGatewayResponse) {
        this.paymentGatewayResponse = paymentGatewayResponse;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

