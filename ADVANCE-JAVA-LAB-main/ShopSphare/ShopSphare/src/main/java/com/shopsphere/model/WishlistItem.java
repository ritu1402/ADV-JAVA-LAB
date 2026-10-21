package com.shopsphere.model;

import java.time.LocalDateTime;

public class WishlistItem {
    private Long wishlistId;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;

    public WishlistItem() {
    }

    public WishlistItem(Long wishlistId, Long userId, Long productId, LocalDateTime createdAt) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public Long getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Long wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

