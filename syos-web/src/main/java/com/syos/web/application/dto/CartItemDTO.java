package com.syos.web.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cart Item DTO with product information
 * Used to return cart items with full product details
 */
public class CartItemDTO {
    private Integer cartId;
    private String productCode;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private BigDecimal subtotal;
    private String imageUrl;
    private boolean inStock;
    private int availableQuantity;
    private LocalDateTime addedAt;

    // Default constructor
    public CartItemDTO() {
    }

    // Full constructor
    public CartItemDTO(Integer cartId, String productCode, String productName,
                       int quantity, BigDecimal unitPrice, BigDecimal originalPrice,
                       BigDecimal discountPercentage, BigDecimal subtotal,
                       String imageUrl, boolean inStock, int availableQuantity,
                       LocalDateTime addedAt) {
        this.cartId = cartId;
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
        this.availableQuantity = availableQuantity;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}