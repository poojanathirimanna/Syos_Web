package com.syos.web.application.dto;

import java.util.List;

/**
 * Complete Cart Response DTO
 * Contains cart items and summary
 */
public class CartResponseDTO {
    private List<CartItemDTO> items;
    private CartSummaryDTO summary;

    // Default constructor
    public CartResponseDTO() {
    }

    // Full constructor
    public CartResponseDTO(List<CartItemDTO> items, CartSummaryDTO summary) {
        this.items = items;
        this.summary = summary;
    }

    // Getters and Setters
    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public CartSummaryDTO getSummary() {
        return summary;
    }

    public void setSummary(CartSummaryDTO summary) {
        this.summary = summary;
    }
}