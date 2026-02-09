package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class SetProductDiscountRequestTest {

    private SetProductDiscountRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new SetProductDiscountRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetDiscountPercentage() {
        BigDecimal discount = new BigDecimal("10.00");
        dto.setDiscountPercentage(discount);
        assertEquals(discount, dto.getDiscountPercentage());
    }

    @Test
    public void testSetAndGetStartDate() {
        LocalDate startDate = LocalDate.now();
        dto.setStartDate(startDate);
        assertEquals(startDate, dto.getStartDate());
    }

    @Test
    public void testSetAndGetEndDate() {
        LocalDate endDate = LocalDate.now().plusDays(30);
        dto.setEndDate(endDate);
        assertEquals(endDate, dto.getEndDate());
    }

    @Test
    public void testValidateSuccess() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(30));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        dto.setProductCode(null);
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        dto.setProductCode("");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        dto.setProductCode("   ");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullDiscountPercentage() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeDiscount() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("-10.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateDiscountOver100() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("101.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateZeroDiscount() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(BigDecimal.ZERO);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidate100PercentDiscount() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("100.00"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateEndDateBeforeStartDate() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setEndDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateSameDateStartAndEnd() {
        LocalDate today = LocalDate.now();
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(today);
        dto.setEndDate(today);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullStartDate() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(null);
        dto.setEndDate(LocalDate.now().plusDays(30));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullEndDate() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(null);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateBothDatesNull() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(null);
        dto.setEndDate(null);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testNullValues() {
        dto.setProductCode(null);
        dto.setDiscountPercentage(null);
        dto.setStartDate(null);
        dto.setEndDate(null);

        assertNull(dto.getProductCode());
        assertNull(dto.getDiscountPercentage());
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        dto.setProductCode(longCode);
        assertEquals(longCode, dto.getProductCode());
    }

    @Test
    public void testDecimalDiscount() {
        dto.setDiscountPercentage(new BigDecimal("15.75"));
        assertEquals(new BigDecimal("15.75"), dto.getDiscountPercentage());
    }

    @Test
    public void testPreciseDiscount() {
        dto.setDiscountPercentage(new BigDecimal("12.345"));
        assertEquals(new BigDecimal("12.345"), dto.getDiscountPercentage());
    }

    @Test
    public void testSmallDiscount() {
        dto.setDiscountPercentage(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), dto.getDiscountPercentage());
    }

    @Test
    public void testStartDateInPast() {
        dto.setStartDate(LocalDate.now().minusDays(10));
        assertEquals(LocalDate.now().minusDays(10), dto.getStartDate());
    }

    @Test
    public void testEndDateInFuture() {
        LocalDate future = LocalDate.now().plusYears(1);
        dto.setEndDate(future);
        assertEquals(future, dto.getEndDate());
    }

    @Test
    public void testCompleteScenario() {
        dto.setProductCode("PROD001");
        dto.setDiscountPercentage(new BigDecimal("25.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(1));

        assertNotNull(dto.getProductCode());
        assertNotNull(dto.getDiscountPercentage());
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getEndDate());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testMultipleValidations() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("20.00"));

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testProductCodeWithDashes() {
        dto.setProductCode("P-001-A");
        assertEquals("P-001-A", dto.getProductCode());
    }

    @Test
    public void testProductCodeWithUnderscores() {
        dto.setProductCode("P_001_A");
        assertEquals("P_001_A", dto.getProductCode());
    }

    @Test
    public void testBoundaryDiscount99_99() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("99.99"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testBoundaryDiscount100_01() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("100.01"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testLongDateRange() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusYears(10));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testShortDateRange() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testDiscountBoundary50() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("50.00"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testDiscountBoundary75() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("75.00"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testDiscountBoundary25() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("25.00"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMinimalRequest() {
        dto.setProductCode("P");
        dto.setDiscountPercentage(BigDecimal.ONE);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testEndDateYesterday() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now().minusDays(10));
        dto.setEndDate(LocalDate.now().minusDays(1));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testStartDateToday() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testEndDateToday() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setStartDate(LocalDate.now().minusDays(5));
        dto.setEndDate(LocalDate.now());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testVerySmallDiscount() {
        dto.setProductCode("P001");
        dto.setDiscountPercentage(new BigDecimal("0.001"));
        assertDoesNotThrow(() -> dto.validate());
    }
}

