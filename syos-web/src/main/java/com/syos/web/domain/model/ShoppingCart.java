package com.syos.web.domain.model;

import java.time.LocalDateTime;

/**
 * Shopping Cart Entity
 * Represents a customer's shopping cart item
 */
public class ShoppingCart {
    private Integer cartId;
    private String userId;
    private String productCode;
    private int quantity;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public ShoppingCart() {
    }

    // Constructor without ID (for creation)
    public ShoppingCart(String userId, String productCode, int quantity) {
        this.userId = userId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    // Full constructor
    public ShoppingCart(Integer cartId, String userId, String productCode,
                        int quantity, LocalDateTime addedAt, LocalDateTime updatedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
    }

    // Validation
    public void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    // Getters and Setters
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}