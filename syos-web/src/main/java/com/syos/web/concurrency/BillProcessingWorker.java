package com.syos.web.concurrency;

import com.syos.web.application.usecases.CreateBillUseCase;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;

/**
 * Worker thread that processes bill requests from the queue
 * Demonstrates explicit multithreading for assignment
 * 🆕 NOW PROCESSES BOTH CASHIER BILLS AND CUSTOMER ORDERS
 */
public class BillProcessingWorker implements Runnable {

    private final RequestQueue<BillRequest> queue;
    private final String workerName;
    private volatile boolean running = true;
    private final CreateBillUseCase createBillUseCase;

    public BillProcessingWorker(RequestQueue<BillRequest> queue, String workerName) {
        this.queue = queue;
        this.workerName = workerName;

        // Initialize use case
        BillDao billDao = new BillDao();
        ProductDao productDao = new ProductDao();
        this.createBillUseCase = new CreateBillUseCase(billDao, productDao);

        System.out.println("✅ Worker thread created: " + workerName);
    }

    @Override
    public void run() {
        System.out.println("🚀 Worker thread started: " + workerName);

        while (running) {
            try {
                // Get next request from queue (blocks if empty)
                BillRequest request = queue.dequeue();

                // 🆕 Log which type of request we're processing
                String requestType = request.getUserType();
                System.out.println("⚙️ [" + workerName + "] Processing " + requestType + " request: " +
                        request.getRequestId());

                long startTime = System.currentTimeMillis();

                try {
                    // 🆕 UPDATED - Pass userType to use case
                    var billDTO = createBillUseCase.execute(
                            request.getBillRequest(),
                            request.getUserId(),
                            request.getUserType()  // 🆕 NEW parameter
                    );

                    long processingTime = System.currentTimeMillis() - startTime;

                    // Complete the request with success
                    BillQueueResponse response = BillQueueResponse.success(
                            "Bill created successfully",
                            billDTO,
                            processingTime
                    );

                    request.complete(response);

                    // 🆕 Enhanced logging
                    System.out.println("✅ [" + workerName + "] " + requestType + " request completed: " +
                            request.getRequestId() + " (" + processingTime + "ms)");

                } catch (Exception e) {
                    long processingTime = System.currentTimeMillis() - startTime;

                    // Complete the request with error
                    BillQueueResponse response = BillQueueResponse.error(
                            e.getMessage(),
                            processingTime
                    );

                    request.complete(response);

                    System.err.println("❌ [" + workerName + "] " + requestType + " request failed: " +
                            request.getRequestId() + " - " + e.getMessage());
                }

            } catch (InterruptedException e) {
                System.out.println("⚠️ [" + workerName + "] Interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("🛑 Worker thread stopped: " + workerName);
    }

    public void stop() {
        running = false;
    }
}