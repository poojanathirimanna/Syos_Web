package com.syos.web.application.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating a bill
 * 🆕 NOW SUPPORTS BOTH CASHIER BILLS AND CUSTOMER ORDERS
 */
public class CreateBillRequest {

    // Common fields (both cashier and customer)
    private String paymentMethod;
    private List<BillItemRequest> items;
    private BigDecimal amountPaid;

    // 🆕 NEW - Customer order specific fields
    private String channel;  // "IN_STORE" or "ONLINE"
    private Integer deliveryAddressId;  // For fetching address
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private String deliveryPhone;
    private String paymentMethodDetails;  // JSON string with card details, etc.
    private String orderStatus;  // "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"
    private String paymentStatus;  // "PENDING", "PAID", "FAILED"

    // 🆕 Inner class for bill items (renamed from BillItem)
    public static class BillItemRequest {
        private String productCode;
        private int quantity;

        public BillItemRequest() {}

        public BillItemRequest(String productCode, int quantity) {
            this.productCode = productCode;
            this.quantity = quantity;
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

    // Default constructor
    public CreateBillRequest() {
        this.channel = "IN_STORE";  // Default to in-store
    }

    // Validation
    public void validate() {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }

        // Validate channel-specific requirements
        if ("ONLINE".equals(channel)) {
            if (deliveryAddressId == null &&
                    (deliveryAddress == null || deliveryCity == null || deliveryPostalCode == null)) {
                throw new IllegalArgumentException("Delivery address is required for online orders");
            }
        }
    }

    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<BillItemRequest> getItems() {
        return items;
    }

    public void setItems(List<BillItemRequest> items) {
        this.items = items;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    // 🆕 NEW Getters and Setters for customer orders
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Integer deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getPaymentMethodDetails() {
        return paymentMethodDetails;
    }

    public void setPaymentMethodDetails(String paymentMethodDetails) {
        this.paymentMethodDetails = paymentMethodDetails;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}