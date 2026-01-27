package com.syos.web.application.dto;

import java.util.List;

/**
 * DTO for creating a new bill
 */
public class CreateBillRequest {
    private String paymentMethod;  // CASH, CARD, etc.
    private List<BillItem> items;

    public CreateBillRequest() {
    }

    public void validate() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Bill must have at least one item");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        for (BillItem item : items) {
            item.validate();
        }
    }

    // Inner class for bill items in request
    public static class BillItem {
        private String productCode;
        private int quantity;

        public BillItem() {
        }

        public BillItem(String productCode, int quantity) {
            this.productCode = productCode;
            this.quantity = quantity;
        }

        public void validate() {
            if (productCode == null || productCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Product code is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
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
    }

    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }
}