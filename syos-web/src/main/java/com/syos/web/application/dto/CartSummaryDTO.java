package com.syos.web.application.dto;

import java.math.BigDecimal;

/**
 * Cart Summary DTO
 * Contains cart totals and summary information
 */
public class CartSummaryDTO {
    private int itemCount;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalAmount;

    // Default constructor
    public CartSummaryDTO() {
        this.itemCount = 0;
        this.subtotal = BigDecimal.ZERO;
        this.totalDiscount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }

    // Full constructor
    public CartSummaryDTO(int itemCount, BigDecimal subtotal,
                          BigDecimal totalDiscount, BigDecimal totalAmount) {
        this.itemCount = itemCount;
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}