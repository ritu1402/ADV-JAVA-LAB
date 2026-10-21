package com.shopsphere.model;

import java.time.LocalDateTime;

public class Address {
    private Long addressId;
    private Long userId;
    private String addressType;
    private Boolean isDefault;
    private String recipientName;
    private String phoneNumber;
    private String streetAddressLine1;
    private String streetAddressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Address() {
    }

    public Address(Long addressId, Long userId, String addressType, Boolean isDefault, String recipientName,
                   String phoneNumber, String streetAddressLine1, String streetAddressLine2, String city,
                   String state, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.addressId = addressId;
        this.userId = userId;
        this.addressType = addressType;
        this.isDefault = isDefault;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.streetAddressLine1 = streetAddressLine1;
        this.streetAddressLine2 = streetAddressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddressLine1() {
        return streetAddressLine1;
    }

    public void setStreetAddressLine1(String streetAddressLine1) {
        this.streetAddressLine1 = streetAddressLine1;
    }

    public String getStreetAddressLine2() {
        return streetAddressLine2;
    }

    public void setStreetAddressLine2(String streetAddressLine2) {
        this.streetAddressLine2 = streetAddressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

