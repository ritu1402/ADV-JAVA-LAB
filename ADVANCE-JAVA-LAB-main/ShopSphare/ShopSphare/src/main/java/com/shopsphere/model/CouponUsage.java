package com.shopsphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponUsage {
    private Long usageId;
    private Integer couponId;
    private Long userId;
    private Long orderId;
    private BigDecimal discountApplied;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    public CouponUsage() {
    }

    public CouponUsage(Long usageId, Integer couponId, Long userId, Long orderId,
                       BigDecimal discountApplied, LocalDateTime usedAt, LocalDateTime createdAt) {
        this.usageId = usageId;
        this.couponId = couponId;
        this.userId = userId;
        this.orderId = orderId;
        this.discountApplied = discountApplied;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public Long getUsageId() {
        return usageId;
    }

    public void setUsageId(Long usageId) {
        this.usageId = usageId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(BigDecimal discountApplied) {
        this.discountApplied = discountApplied;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

