package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiveStockRequestTest {

    private ReceiveStockRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new ReceiveStockRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        LocalDate purchaseDate = LocalDate.now();
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        ReceiveStockRequest request = new ReceiveStockRequest("P001", 100, purchaseDate, expiryDate);

        assertEquals("P001", request.getProductCode());
        assertEquals(100, request.getQuantity());
        assertEquals(purchaseDate, request.getPurchaseDate());
        assertEquals(expiryDate, request.getExpiryDate());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetQuantity() {
        dto.setQuantity(100);
        assertEquals(100, dto.getQuantity());
    }

    @Test
    public void testSetAndGetPurchaseDate() {
        LocalDate date = LocalDate.now();
        dto.setPurchaseDate(date);
        assertEquals(date, dto.getPurchaseDate());
    }

    @Test
    public void testSetAndGetExpiryDate() {
        LocalDate date = LocalDate.now().plusDays(30);
        dto.setExpiryDate(date);
        assertEquals(date, dto.getExpiryDate());
    }

    @Test
    public void testValidateSuccess() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusDays(30));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        dto.setProductCode(null);
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        dto.setProductCode("");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        dto.setProductCode("   ");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateZeroQuantity() {
        dto.setProductCode("P001");
        dto.setQuantity(0);
        dto.setPurchaseDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeQuantity() {
        dto.setProductCode("P001");
        dto.setQuantity(-10);
        dto.setPurchaseDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullPurchaseDate() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateFuturePurchaseDate() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateExpiryBeforePurchase() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWithNullExpiryDate() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        dto.setExpiryDate(null);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWithPastPurchaseDate() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().minusDays(10));
        dto.setExpiryDate(LocalDate.now().plusDays(20));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateTodayPurchaseDate() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateSameDatePurchaseAndExpiry() {
        LocalDate today = LocalDate.now();
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(today);
        dto.setExpiryDate(today);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testNullValues() {
        dto.setProductCode(null);
        dto.setPurchaseDate(null);
        dto.setExpiryDate(null);

        assertNull(dto.getProductCode());
        assertNull(dto.getPurchaseDate());
        assertNull(dto.getExpiryDate());
    }

    @Test
    public void testLargeQuantity() {
        dto.setQuantity(1000000);
        assertEquals(1000000, dto.getQuantity());
    }

    @Test
    public void testSmallQuantity() {
        dto.setQuantity(1);
        assertEquals(1, dto.getQuantity());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        dto.setProductCode(longCode);
        assertEquals(longCode, dto.getProductCode());
    }

    @Test
    public void testProductCodeWithSpecialChars() {
        dto.setProductCode("P-001_A");
        assertEquals("P-001_A", dto.getProductCode());
    }

    @Test
    public void testExpiryDateFarFuture() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusYears(10));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testExpiryDateNextDay() {
        LocalDate today = LocalDate.now();
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(today);
        dto.setExpiryDate(today.plusDays(1));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testMultipleValidations() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now());

        assertDoesNotThrow(() -> dto.validate());
        assertDoesNotThrow(() -> dto.validate());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testCompleteScenario() {
        dto.setProductCode("PROD001");
        dto.setQuantity(500);
        dto.setPurchaseDate(LocalDate.now().minusDays(5));
        dto.setExpiryDate(LocalDate.now().plusDays(90));

        assertNotNull(dto.getProductCode());
        assertTrue(dto.getQuantity() > 0);
        assertNotNull(dto.getPurchaseDate());
        assertNotNull(dto.getExpiryDate());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testPurchaseDateOneYearAgo() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().minusYears(1));
        dto.setExpiryDate(LocalDate.now().plusDays(30));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testExpiryDateYesterday() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().minusDays(10));
        dto.setExpiryDate(LocalDate.now().minusDays(1));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testQuantityBoundary() {
        dto.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getQuantity());
    }

    @Test
    public void testQuantityBoundaryMin() {
        dto.setQuantity(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, dto.getQuantity());
    }

    @Test
    public void testProductCodeEmpty() {
        dto.setProductCode("");
        assertEquals("", dto.getProductCode());
    }

    @Test
    public void testProductCodeWhitespace() {
        dto.setProductCode("   ");
        assertEquals("   ", dto.getProductCode());
    }

    @Test
    public void testPurchaseDateTomorrow() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testPurchaseDateNextWeek() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().plusWeeks(1));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWithAllNullFields() {
        dto.setProductCode(null);
        dto.setQuantity(0);
        dto.setPurchaseDate(null);
        dto.setExpiryDate(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateExpiryEqualsToday() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setPurchaseDate(LocalDate.now().minusDays(1));
        dto.setExpiryDate(LocalDate.now());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidQuantities() {
        int[] validQuantities = {1, 10, 100, 1000, 10000, 99999};
        for (int qty : validQuantities) {
            dto.setProductCode("P001");
            dto.setQuantity(qty);
            dto.setPurchaseDate(LocalDate.now());
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testInvalidQuantities() {
        int[] invalidQuantities = {0, -1, -10, -100};
        for (int qty : invalidQuantities) {
            dto.setProductCode("P001");
            dto.setQuantity(qty);
            dto.setPurchaseDate(LocalDate.now());
            assertThrows(IllegalArgumentException.class, () -> dto.validate());
        }
    }
}

