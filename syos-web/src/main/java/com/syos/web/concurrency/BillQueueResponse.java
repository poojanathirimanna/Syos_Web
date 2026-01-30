package com.syos.web.concurrency;

import com.syos.web.application.dto.BillDTO;

/**
 * Response from bill processing queue
 * (Different from dto.BillResponse which is for API responses)
 */
public class BillQueueResponse {

    private final boolean success;
    private final String message;
    private final BillDTO billDTO;
    private final String errorMessage;
    private final long processingTimeMs;

    private BillQueueResponse(boolean success, String message, BillDTO billDTO,
                              String errorMessage, long processingTimeMs) {
        this.success = success;
        this.message = message;
        this.billDTO = billDTO;
        this.errorMessage = errorMessage;
        this.processingTimeMs = processingTimeMs;
    }

    public static BillQueueResponse success(String message, BillDTO billDTO, long processingTimeMs) {
        return new BillQueueResponse(true, message, billDTO, null, processingTimeMs);
    }

    public static BillQueueResponse error(String errorMessage, long processingTimeMs) {
        return new BillQueueResponse(false, null, null, errorMessage, processingTimeMs);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BillDTO getBillDTO() {
        return billDTO;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
}