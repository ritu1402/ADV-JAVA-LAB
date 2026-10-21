package com.shopsphere.service;

import java.io.Serializable;

/**
 * Data Transfer Object containing checkout parameters submitted by the client.
 * NOTE: Intentionally omits any pricing, discounts, or total amounts to ensure
 * all monetary calculations are strictly computed server-side.
 */
public class CheckoutRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long shippingAddressId;
    private Long billingAddressId;
    private String couponCode;
    private String paymentMethod;
    private String notes;

    public CheckoutRequest() {
    }

    public CheckoutRequest(Long userId, Long shippingAddressId, Long billingAddressId,
                           String couponCode, String paymentMethod, String notes) {
        this.userId = userId;
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.couponCode = couponCode;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
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

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

