package com.syos.web.concurrency;

import com.syos.web.application.dto.CreateBillRequest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a bill creation request in the queue
 * 🆕 NOW SUPPORTS BOTH CASHIER AND CUSTOMER REQUESTS
 */
public class BillRequest {

    private final String requestId;
    private final CreateBillRequest billRequest;
    private final String userId;
    private final String userType;  // 🆕 NEW: "CASHIER" or "CUSTOMER"
    private final long timestamp;
    private final CompletableFuture<BillQueueResponse> future;

    // 🆕 UPDATED constructor with userType
    public BillRequest(CreateBillRequest billRequest, String userId, String userType) {
        this.requestId = UUID.randomUUID().toString();
        this.billRequest = billRequest;
        this.userId = userId;
        this.userType = userType;  // 🆕 NEW
        this.timestamp = System.currentTimeMillis();
        this.future = new CompletableFuture<>();
    }

    // Getters
    public String getRequestId() {
        return requestId;
    }

    public CreateBillRequest getBillRequest() {
        return billRequest;
    }

    public String getUserId() {
        return userId;
    }

    // 🆕 NEW getter
    public String getUserType() {
        return userType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public CompletableFuture<BillQueueResponse> getFuture() {
        return future;
    }

    /**
     * Complete the request with result
     */
    public void complete(BillQueueResponse response) {
        future.complete(response);
    }

    /**
     * Complete the request with error
     */
    public void completeExceptionally(Throwable ex) {
        future.completeExceptionally(ex);
    }
}