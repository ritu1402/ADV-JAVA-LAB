package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private Long shippingAddressId;
    private Long billingAddressId;
    private Integer couponId;
    private String orderStatus;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Order(Long orderId, String orderNumber, Long userId, Long shippingAddressId,
                 Long billingAddressId, Integer couponId, String orderStatus, BigDecimal subtotal,
                 BigDecimal discountAmount, BigDecimal shippingFee, BigDecimal taxAmount,
                 BigDecimal totalAmount, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.couponId = couponId;
        this.orderStatus = orderStatus;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.shippingFee = shippingFee;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public Long getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

