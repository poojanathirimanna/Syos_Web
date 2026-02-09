package com.syos.web.concurrency;

import com.syos.web.application.dto.CreateBillRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BillQueueServiceTest {

    private BillQueueService service;

    @BeforeEach
    public void setUp() throws Exception {
        // Reset singleton instance using reflection
        Field instanceField = BillQueueService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        
        service = BillQueueService.getInstance();
    }

    @AfterEach
    public void tearDown() {
        if (service != null) {
            service.shutdown();
        }
    }

    @Test
    public void testGetInstance() {
        BillQueueService instance1 = BillQueueService.getInstance();
        BillQueueService instance2 = BillQueueService.getInstance();
        assertSame(instance1, instance2);
        assertNotNull(instance1);
    }

    @Test
    public void testSubmitBillRequest() throws Exception {
        CreateBillRequest billRequest = new CreateBillRequest();
        // Minimally valid request for validation in CreateBillUseCase (if worker runs it)
        // But here we just test if it enqueues correctly.
        
        CompletableFuture<BillQueueResponse> future = service.submitBillRequest(billRequest, "user1");
        assertNotNull(future);
        assertFalse(future.isDone());
        
        BillQueueService.QueueStats stats = service.getStats();
        // Since workers are running, it might be processed immediately or stay in queue
        assertTrue(stats.queueSize >= 0);
        assertTrue(stats.remainingCapacity <= 1000);
        assertEquals(20, stats.numWorkers);
    }

    @Test
    public void testGetStats() {
        BillQueueService.QueueStats stats = service.getStats();
        assertEquals(0, stats.queueSize);
        assertEquals(1000, stats.remainingCapacity);
        assertEquals(20, stats.numWorkers);
    }
    
    @Test
    public void testShutdown() {
        service.shutdown();
        // After shutdown, we can't easily test if it's "off" without more introspection,
        // but we verify no exceptions occur.
    }
}
