package com.shopsphere.model;

import java.time.LocalDateTime;

public class Review {
    private Long reviewId;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String title;
    private String comment;
    private Boolean isVerifiedPurchase;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review() {
    }

    public Review(Long reviewId, Long productId, Long userId, Integer rating, String title,
                  String comment, Boolean isVerifiedPurchase, String status,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.isVerifiedPurchase = isVerifiedPurchase;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIsVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setIsVerifiedPurchase(Boolean isVerifiedPurchase) {
        this.isVerifiedPurchase = isVerifiedPurchase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

