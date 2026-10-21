package com.shopsphere.service;

import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.Payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CheckoutResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Order order;
    private Payment payment;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public CheckoutResult() {
    }

    public CheckoutResult(Order order, Payment payment, List<OrderItem> items, BigDecimal subtotal,
                          BigDecimal discountAmount, BigDecimal shippingFee, BigDecimal taxAmount,
                          BigDecimal totalAmount) {
        this.order = order;
        this.payment = payment;
        this.items = items;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.shippingFee = shippingFee;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
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
}

