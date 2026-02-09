package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateCartRequestTest {

    private UpdateCartRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new UpdateCartRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        UpdateCartRequest request = new UpdateCartRequest(5);
        assertEquals(5, request.getQuantity());
    }

    @Test
    public void testSetAndGetQuantity() {
        dto.setQuantity(5);
        assertEquals(5, dto.getQuantity());
    }

    @Test
    public void testValidateSuccess() {
        dto.setQuantity(5);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateZeroQuantity() {
        dto.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeQuantity() {
        dto.setQuantity(-5);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateQuantityOne() {
        dto.setQuantity(1);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testLargeQuantity() {
        dto.setQuantity(10000);
        assertEquals(10000, dto.getQuantity());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testQuantityBoundary() {
        dto.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getQuantity());
    }

    @Test
    public void testMultipleUpdates() {
        dto.setQuantity(5);
        assertEquals(5, dto.getQuantity());

        dto.setQuantity(10);
        assertEquals(10, dto.getQuantity());
    }

    @Test
    public void testQuantityIncrement() {
        dto.setQuantity(5);
        int original = dto.getQuantity();
        dto.setQuantity(original + 1);
        assertEquals(6, dto.getQuantity());
    }

    @Test
    public void testQuantityDecrement() {
        dto.setQuantity(10);
        int original = dto.getQuantity();
        dto.setQuantity(original - 1);
        assertEquals(9, dto.getQuantity());
    }

    @Test
    public void testQuantitySequential() {
        for (int i = 1; i <= 10; i++) {
            dto.setQuantity(i);
            assertEquals(i, dto.getQuantity());
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testMultipleObjects() {
        UpdateCartRequest dto1 = new UpdateCartRequest();
        dto1.setQuantity(5);

        UpdateCartRequest dto2 = new UpdateCartRequest();
        dto2.setQuantity(10);

        assertNotEquals(dto1.getQuantity(), dto2.getQuantity());
    }

    @Test
    public void testSameValues() {
        UpdateCartRequest dto1 = new UpdateCartRequest();
        dto1.setQuantity(5);

        UpdateCartRequest dto2 = new UpdateCartRequest();
        dto2.setQuantity(5);

        assertEquals(dto1.getQuantity(), dto2.getQuantity());
    }

    @Test
    public void testValidateExceptionMessage() {
        dto.setQuantity(0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> dto.validate());
        assertTrue(exception.getMessage().contains("greater than 0"));
    }

    @Test
    public void testQuantityTwo() {
        dto.setQuantity(2);
        assertEquals(2, dto.getQuantity());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testQuantityHundred() {
        dto.setQuantity(100);
        assertEquals(100, dto.getQuantity());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testQuantityFifty() {
        dto.setQuantity(50);
        assertEquals(50, dto.getQuantity());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMultipleTimes() {
        dto.setQuantity(5);
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testConstructorWithOne() {
        UpdateCartRequest request = new UpdateCartRequest(1);
        assertEquals(1, request.getQuantity());
        assertDoesNotThrow(() -> request.validate());
    }

    @Test
    public void testConstructorWithZero() {
        UpdateCartRequest request = new UpdateCartRequest(0);
        assertThrows(IllegalArgumentException.class, () -> request.validate());
    }

    @Test
    public void testConstructorWithNegative() {
        UpdateCartRequest request = new UpdateCartRequest(-5);
        assertThrows(IllegalArgumentException.class, () -> request.validate());
    }

    @Test
    public void testConstructorWithLarge() {
        UpdateCartRequest request = new UpdateCartRequest(1000);
        assertEquals(1000, request.getQuantity());
        assertDoesNotThrow(() -> request.validate());
    }

    @Test
    public void testQuantityTwenty() {
        dto.setQuantity(20);
        assertEquals(20, dto.getQuantity());
    }

    @Test
    public void testQuantityThirty() {
        dto.setQuantity(30);
        assertEquals(30, dto.getQuantity());
    }

    @Test
    public void testQuantityForty() {
        dto.setQuantity(40);
        assertEquals(40, dto.getQuantity());
    }

    @Test
    public void testQuantitySixty() {
        dto.setQuantity(60);
        assertEquals(60, dto.getQuantity());
    }

    @Test
    public void testQuantitySeventy() {
        dto.setQuantity(70);
        assertEquals(70, dto.getQuantity());
    }

    @Test
    public void testQuantityEighty() {
        dto.setQuantity(80);
        assertEquals(80, dto.getQuantity());
    }

    @Test
    public void testQuantityNinety() {
        dto.setQuantity(90);
        assertEquals(90, dto.getQuantity());
    }

    @Test
    public void testQuantityRange() {
        int[] quantities = {1, 5, 10, 25, 50, 75, 100};
        for (int qty : quantities) {
            dto.setQuantity(qty);
            assertEquals(qty, dto.getQuantity());
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testInvalidQuantityRange() {
        int[] quantities = {0, -1, -5, -10, -100};
        for (int qty : quantities) {
            dto.setQuantity(qty);
            assertThrows(IllegalArgumentException.class, () -> dto.validate());
        }
    }

    @Test
    public void testQuantityMinValue() {
        dto.setQuantity(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, dto.getQuantity());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testQuantityMaxValue() {
        dto.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getQuantity());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testQuantityUpdateSequence() {
        dto.setQuantity(1);
        assertEquals(1, dto.getQuantity());

        dto.setQuantity(2);
        assertEquals(2, dto.getQuantity());

        dto.setQuantity(5);
        assertEquals(5, dto.getQuantity());

        dto.setQuantity(10);
        assertEquals(10, dto.getQuantity());
    }

    @Test
    public void testCompleteRequest() {
        dto.setQuantity(25);
        assertNotNull(dto);
        assertTrue(dto.getQuantity() > 0);
        assertDoesNotThrow(() -> dto.validate());
    }
}

