package com.syos.web.application.dto;

/**
 * DTO for Cashier display
 */
public class CashierDTO {
    private String userId;
    private String fullName;
    private String email;
    private String contactNumber;
    private boolean isActive;

    public CashierDTO() {
    }

    public CashierDTO(String userId, String fullName, String email, String contactNumber, boolean isActive) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}