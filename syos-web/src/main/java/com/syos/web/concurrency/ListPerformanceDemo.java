package com.syos.web.concurrency;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Demonstration of when to use ArrayList vs LinkedList in concurrent systems
 * This shows the performance characteristics for different operations
 */
public class ListPerformanceDemo {

    /**
     * Example: Request Priority Queue (if you had one)
     * LinkedList would be better here because we frequently insert at beginning
     */
    public static class PriorityRequestHandler {

        // ❌ BAD - ArrayList for frequent head insertions
        private List<String> urgentRequests_ArrayList = new ArrayList<>();

        // ✅ GOOD - LinkedList for frequent head insertions
        private List<String> urgentRequests_LinkedList = new LinkedList<>();

        // ✅ BEST - Use proper concurrent data structure
        private ConcurrentLinkedQueue<String> urgentRequests_Concurrent = new ConcurrentLinkedQueue<>();

        public void addUrgentRequest(String requestId) {
            // ArrayList: O(n) - has to shift all elements
            urgentRequests_ArrayList.add(0, requestId);

            // LinkedList: O(1) - just change head pointer
            urgentRequests_LinkedList.add(0, requestId);

            // ConcurrentLinkedQueue: O(1) and thread-safe
            urgentRequests_Concurrent.offer(requestId);
        }

        public String getNextUrgentRequest() {
            if (!urgentRequests_LinkedList.isEmpty()) {
                // LinkedList: O(1) for head removal
                return urgentRequests_LinkedList.remove(0);
            }
            return null;
        }
    }

    /**
     * Example: Your Current BillQueueService Usage
     * ArrayList is correct here because:
     * - Fixed size (20 workers)
     * - Initialized once
     * - No frequent modifications
     */
    public static class WorkerManager {

        // ✅ CORRECT - ArrayList for fixed collections
        private final List<Thread> workerThreads = new ArrayList<>();
        private final List<String> workerNames = new ArrayList<>();

        public void initializeWorkers(int numWorkers) {
            // This happens only once during startup
            for (int i = 0; i < numWorkers; i++) {
                String workerName = "Worker-" + (i + 1);
                Thread worker = new Thread(() -> {
                    // Worker logic here
                });

                workerThreads.add(worker);  // O(1) amortized - fine for ArrayList
                workerNames.add(workerName); // O(1) amortized - fine for ArrayList

                worker.start();
            }
        }

        // Access by index is O(1) with ArrayList vs O(n) with LinkedList
        public Thread getWorker(int index) {
            return workerThreads.get(index); // ArrayList is better here
        }
    }

    /**
     * Performance comparison demonstration
     */
    public static void demonstratePerformance() {
        System.out.println("📊 ArrayList vs LinkedList Performance Comparison:");

        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();

        // Test 1: Adding to end (both are good)
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayList.add("Item " + i);
        }
        long arrayListAddEnd = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedList.add("Item " + i);
        }
        long linkedListAddEnd = System.nanoTime() - start;

        // Test 2: Adding to beginning (LinkedList wins)
        arrayList.clear();
        linkedList.clear();

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.add(0, "Item " + i);  // O(n) - slow!
        }
        long arrayListAddBeginning = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.add(0, "Item " + i);  // O(1) - fast!
        }
        long linkedListAddBeginning = System.nanoTime() - start;

        System.out.println("Adding to END:");
        System.out.println("  ArrayList: " + arrayListAddEnd / 1_000_000 + "ms");
        System.out.println("  LinkedList: " + linkedListAddEnd / 1_000_000 + "ms");

        System.out.println("Adding to BEGINNING:");
        System.out.println("  ArrayList: " + arrayListAddBeginning / 1_000_000 + "ms");
        System.out.println("  LinkedList: " + linkedListAddBeginning / 1_000_000 + "ms");
    }
}
