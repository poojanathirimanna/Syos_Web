package com.syos.web.application.dto;

import java.util.List;

/**
 * DTO for transfer stock response
 * Shows which batches were used in the transfer
 */
public class TransferStockResponse {
    private String productCode;
    private int totalQuantityTransferred;
    private String fromLocation;
    private String toLocation;
    private List<BatchTransfer> batchesUsed;
    private String message;

    public TransferStockResponse(String productCode, int totalQuantityTransferred,
                                 String fromLocation, String toLocation,
                                 List<BatchTransfer> batchesUsed, String message) {
        this.productCode = productCode;
        this.totalQuantityTransferred = totalQuantityTransferred;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.batchesUsed = batchesUsed;
        this.message = message;
    }

    // Inner class for batch transfer details
    public static class BatchTransfer {
        private int batchId;
        private int quantityTransferred;
        private String expiryDate;

        public BatchTransfer(int batchId, int quantityTransferred, String expiryDate) {
            this.batchId = batchId;
            this.quantityTransferred = quantityTransferred;
            this.expiryDate = expiryDate;
        }

        public int getBatchId() {
            return batchId;
        }

        public void setBatchId(int batchId) {
            this.batchId = batchId;
        }

        public int getQuantityTransferred() {
            return quantityTransferred;
        }

        public void setQuantityTransferred(int quantityTransferred) {
            this.quantityTransferred = quantityTransferred;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getTotalQuantityTransferred() {
        return totalQuantityTransferred;
    }

    public void setTotalQuantityTransferred(int totalQuantityTransferred) {
        this.totalQuantityTransferred = totalQuantityTransferred;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public List<BatchTransfer> getBatchesUsed() {
        return batchesUsed;
    }

    public void setBatchesUsed(List<BatchTransfer> batchesUsed) {
        this.batchesUsed = batchesUsed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}