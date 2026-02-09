package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class StockBatchDTOTest {

    private StockBatchDTO dto;

    @BeforeEach
    public void setUp() {
        // StockBatchDTO requires constructor with 7 arguments
        dto = new StockBatchDTO(1, "P001", "Product 1", LocalDate.now(),
                LocalDate.now().plusDays(30), 100, new BigDecimal("10.00"));
    }

    @Test
    public void testConstructor() {
        assertNotNull(dto);
        assertEquals(1, dto.getBatchId());
        assertEquals("P001", dto.getProductCode());
        assertEquals("Product 1", dto.getProductName());
        assertEquals(100, dto.getAvailableQuantity());
    }

    @Test
    public void testSetAndGetBatchId() {
        dto.setBatchId(2);
        assertEquals(2, dto.getBatchId());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P002");
        assertEquals("P002", dto.getProductCode());
    }

    @Test
    public void testSetAndGetProductName() {
        dto.setProductName("Product 2");
        assertEquals("Product 2", dto.getProductName());
    }

    @Test
    public void testSetAndGetPurchaseDate() {
        LocalDate purchaseDate = LocalDate.now().minusDays(10);
        dto.setPurchaseDate(purchaseDate);
        assertEquals(purchaseDate, dto.getPurchaseDate());
    }

    @Test
    public void testSetAndGetExpiryDate() {
        LocalDate expiryDate = LocalDate.now().plusDays(60);
        dto.setExpiryDate(expiryDate);
        assertEquals(expiryDate, dto.getExpiryDate());
    }

    @Test
    public void testSetAndGetAvailableQuantity() {
        dto.setAvailableQuantity(200);
        assertEquals(200, dto.getAvailableQuantity());
    }

    @Test
    public void testSetAndGetDiscountPercentage() {
        BigDecimal discount = new BigDecimal("15.00");
        dto.setDiscountPercentage(discount);
        assertEquals(discount, dto.getDiscountPercentage());
    }

    @Test
    public void testSetAndGetDaysUntilExpiry() {
        dto.setDaysUntilExpiry(45);
        assertEquals(45, dto.getDaysUntilExpiry());
    }

    @Test
    public void testSetAndGetExpired() {
        dto.setExpired(true);
        assertTrue(dto.isExpired());
    }

    @Test
    public void testSetAndGetNearExpiry() {
        dto.setNearExpiry(true);
        assertTrue(dto.isNearExpiry());
    }

    @Test
    public void testNullBatchId() {
        dto.setBatchId(null);
        assertNull(dto.getBatchId());
    }

    @Test
    public void testNullProductCode() {
        dto.setProductCode(null);
        assertNull(dto.getProductCode());
    }

    @Test
    public void testNullProductName() {
        dto.setProductName(null);
        assertNull(dto.getProductName());
    }

    @Test
    public void testEmptyProductCode() {
        dto.setProductCode("");
        assertEquals("", dto.getProductCode());
    }

    @Test
    public void testZeroAvailableQuantity() {
        dto.setAvailableQuantity(0);
        assertEquals(0, dto.getAvailableQuantity());
    }

    @Test
    public void testNegativeAvailableQuantity() {
        dto.setAvailableQuantity(-10);
        assertEquals(-10, dto.getAvailableQuantity());
    }

    @Test
    public void testLargeAvailableQuantity() {
        dto.setAvailableQuantity(100000);
        assertEquals(100000, dto.getAvailableQuantity());
    }

    @Test
    public void testNullExpiryDate() {
        dto.setExpiryDate(null);
        assertNull(dto.getExpiryDate());
    }

    @Test
    public void testNullPurchaseDate() {
        dto.setPurchaseDate(null);
        assertNull(dto.getPurchaseDate());
    }

    @Test
    public void testNullDiscountPercentage() {
        dto.setDiscountPercentage(null);
        assertNull(dto.getDiscountPercentage());
    }

    @Test
    public void testZeroDiscountPercentage() {
        dto.setDiscountPercentage(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, dto.getDiscountPercentage());
    }

    @Test
    public void testLargeDiscountPercentage() {
        BigDecimal discount = new BigDecimal("99.99");
        dto.setDiscountPercentage(discount);
        assertEquals(discount, dto.getDiscountPercentage());
    }

    @Test
    public void testExpiredFalse() {
        dto.setExpired(false);
        assertFalse(dto.isExpired());
    }

    @Test
    public void testNearExpiryFalse() {
        dto.setNearExpiry(false);
        assertFalse(dto.isNearExpiry());
    }

    @Test
    public void testNegativeDaysUntilExpiry() {
        dto.setDaysUntilExpiry(-5);
        assertEquals(-5, dto.getDaysUntilExpiry());
    }

    @Test
    public void testCompleteScenario() {
        dto.setBatchId(5);
        dto.setProductCode("P005");
        dto.setProductName("Test Product");
        dto.setAvailableQuantity(500);
        dto.setDiscountPercentage(new BigDecimal("20.00"));
        dto.setDaysUntilExpiry(15);
        dto.setNearExpiry(true);
        dto.setExpired(false);

        assertNotNull(dto.getBatchId());
        assertNotNull(dto.getProductCode());
        assertEquals(500, dto.getAvailableQuantity());
        assertTrue(dto.isNearExpiry());
        assertFalse(dto.isExpired());
    }

    @Test
    public void testConstructorWithNullExpiryDate() {
        StockBatchDTO nullExpiryDto = new StockBatchDTO(2, "P002", "Product 2",
                LocalDate.now(), null, 50, new BigDecimal("5.00"));

        assertFalse(nullExpiryDto.isExpired());
        assertFalse(nullExpiryDto.isNearExpiry());
        assertEquals(Integer.MAX_VALUE, nullExpiryDto.getDaysUntilExpiry());
    }

    @Test
    public void testConstructorWithPastExpiryDate() {
        StockBatchDTO expiredDto = new StockBatchDTO(3, "P003", "Product 3",
                LocalDate.now().minusDays(60), LocalDate.now().minusDays(10), 20, new BigDecimal("50.00"));

        assertTrue(expiredDto.isExpired());
        assertTrue(expiredDto.getDaysUntilExpiry() < 0);
    }

    @Test
    public void testConstructorWithNearExpiryDate() {
        StockBatchDTO nearExpiryDto = new StockBatchDTO(4, "P004", "Product 4",
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(15), 30, new BigDecimal("25.00"));

        assertFalse(nearExpiryDto.isExpired());
        assertTrue(nearExpiryDto.isNearExpiry());
    }
}

