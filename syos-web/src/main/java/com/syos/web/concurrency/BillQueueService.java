package com.syos.web.concurrency;

import com.syos.web.application.dto.CreateBillRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service that manages the bill processing queue and worker threads
 * Singleton pattern for application-wide access
 * 🆕 NOW HANDLES BOTH CASHIER BILLS AND CUSTOMER ORDERS
 */
public class BillQueueService {

    private static BillQueueService instance;

    private final RequestQueue<BillRequest> queue;
    private final List<Thread> workerThreads;
    private final List<BillProcessingWorker> workers;
    private final int numWorkers;

    private BillQueueService(int queueSize, int numWorkers) {
        this.queue = new RequestQueue<>(queueSize);
        this.numWorkers = numWorkers;
        this.workerThreads = new ArrayList<>();
        this.workers = new ArrayList<>();

        // Start worker threads
        startWorkers();

        System.out.println("✅ BillQueueService initialized with " + numWorkers + " workers, queue capacity: " + queueSize);
    }

    public static synchronized BillQueueService getInstance() {
        if (instance == null) {
            instance = new BillQueueService(1000, 20);
            // Queue size: 1000 requests (handles both cashier + customer)
            // Workers: 20 threads (processes both types)
        }
        return instance;
    }

    private void startWorkers() {
        for (int i = 0; i < numWorkers; i++) {
            String workerName = "BillWorker-" + (i + 1);
            BillProcessingWorker worker = new BillProcessingWorker(queue, workerName);
            Thread thread = new Thread(worker, workerName);

            workers.add(worker);
            workerThreads.add(thread);

            thread.start();
        }
    }

    /**
     * Submit a bill request for processing (CASHIER - default)
     * Returns a CompletableFuture that will be completed when processing is done
     */
    public CompletableFuture<BillQueueResponse> submitBillRequest(
            CreateBillRequest billRequest, String userId) {
        return submitBillRequest(billRequest, userId, "CASHIER");
    }

    /**
     * 🆕 Submit a bill request for processing (GENERIC - supports both CASHIER and CUSTOMER)
     * Returns a CompletableFuture that will be completed when processing is done
     */
    public CompletableFuture<BillQueueResponse> submitBillRequest(
            CreateBillRequest billRequest, String userId, String userType) {

        BillRequest request = new BillRequest(billRequest, userId, userType);

        try {
            // Add to queue (blocks if queue is full)
            queue.enqueue(request);

            System.out.println("📨 [" + userType + "] Request submitted: " + request.getRequestId() +
                    " | Queue size: " + queue.size() + "/" + (queue.size() + queue.remainingCapacity()));

            return request.getFuture();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            CompletableFuture<BillQueueResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Request submission interrupted"));
            return future;
        }
    }

    /**
     * Get queue statistics
     */
    public QueueStats getStats() {
        return new QueueStats(
                queue.size(),
                queue.remainingCapacity(),
                numWorkers
        );
    }

    /**
     * Shutdown the service
     */
    public void shutdown() {
        System.out.println("🛑 Shutting down BillQueueService...");

        // Stop all workers
        for (BillProcessingWorker worker : workers) {
            worker.stop();
        }

        // Interrupt all threads
        for (Thread thread : workerThreads) {
            thread.interrupt();
        }

        // Wait for threads to finish
        for (Thread thread : workerThreads) {
            try {
                thread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("✅ BillQueueService shutdown complete");
    }

    public static class QueueStats {
        public final int queueSize;
        public final int remainingCapacity;
        public final int numWorkers;

        public QueueStats(int queueSize, int remainingCapacity, int numWorkers) {
            this.queueSize = queueSize;
            this.remainingCapacity = remainingCapacity;
            this.numWorkers = numWorkers;
        }
    }
}