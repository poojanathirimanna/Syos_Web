package com.syos.web.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safe request queue for handling concurrent bill creation
 * Demonstrates explicit concurrency control for assignment
 */
public class RequestQueue<T> {

    private final BlockingQueue<T> queue;
    private final int maxSize;

    public RequestQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedBlockingQueue<>(maxSize);
        System.out.println("✅ RequestQueue initialized with capacity: " + maxSize);
    }

    /**
     * Add request to queue (blocks if queue is full)
     */
    public void enqueue(T request) throws InterruptedException {
        queue.put(request);
        System.out.println("📥 Request queued. Queue size: " + queue.size());
    }

    /**
     * Get next request from queue (blocks if queue is empty)
     */
    public T dequeue() throws InterruptedException {
        T request = queue.take();
        System.out.println("📤 Request dequeued. Queue size: " + queue.size());
        return request;
    }

    /**
     * Get current queue size
     */
    public int size() {
        return queue.size();
    }

    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Get remaining capacity
     */
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }
}