package com.syos.web.application.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PromotionDTOTest {

    private PromotionDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new PromotionDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testProductLevelConstructor() {
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");
        BigDecimal discountPercentage = new BigDecimal("20.00");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);

        PromotionDTO productPromo = new PromotionDTO("P001", "Product 1", originalPrice,
                discountedPrice, discountPercentage, startDate, endDate);

        assertEquals("PRODUCT", productPromo.getType());
        assertEquals("P001", productPromo.getProductCode());
        assertEquals("Product 1", productPromo.getProductName());
        assertEquals(originalPrice, productPromo.getOriginalPrice());
        assertEquals(discountedPrice, productPromo.getDiscountedPrice());
        assertEquals(discountPercentage, productPromo.getDiscountPercentage());
    }

    @Test
    public void testSetAndGetType() {
        dto.setType("PRODUCT");
        assertEquals("PRODUCT", dto.getType());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetProductName() {
        dto.setProductName("Test Product");
        assertEquals("Test Product", dto.getProductName());
    }

    @Test
    public void testSetAndGetBatchId() {
        dto.setBatchId(123);
        assertEquals(123, dto.getBatchId());
    }

    @Test
    public void testSetAndGetExpiryDate() {
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        dto.setExpiryDate(expiryDate);
        assertEquals(expiryDate, dto.getExpiryDate());
    }

    @Test
    public void testSetAndGetOriginalPrice() {
        BigDecimal price = new BigDecimal("99.99");
        dto.setOriginalPrice(price);
        assertEquals(price, dto.getOriginalPrice());
    }

    @Test
    public void testSetAndGetDiscountedPrice() {
        BigDecimal price = new BigDecimal("79.99");
        dto.setDiscountedPrice(price);
        assertEquals(price, dto.getDiscountedPrice());
    }

    @Test
    public void testSetAndGetDiscountPercentage() {
        BigDecimal percentage = new BigDecimal("20.00");
        dto.setDiscountPercentage(percentage);
        assertEquals(percentage, dto.getDiscountPercentage());
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
    public void testSetAndGetDaysUntilExpiry() {
        dto.setDaysUntilExpiry(15);
        assertEquals(15, dto.getDaysUntilExpiry());
    }

    @Test
    public void testSetAndGetIsNearExpiry() {
        dto.setNearExpiry(true);
        assertTrue(dto.isNearExpiry());
    }

    @Test
    public void testNullBatchId() {
        dto.setBatchId(null);
        assertNull(dto.getBatchId());
    }

    @Test
    public void testNullExpiryDate() {
        dto.setExpiryDate(null);
        assertNull(dto.getExpiryDate());
    }

    @Test
    public void testProductTypePromotion() {
        dto.setType("PRODUCT");
        assertEquals("PRODUCT", dto.getType());
    }

    @Test
    public void testBatchTypePromotion() {
        dto.setType("BATCH");
        assertEquals("BATCH", dto.getType());
    }

    @Test
    public void testZeroDiscountPercentage() {
        dto.setDiscountPercentage(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, dto.getDiscountPercentage());
    }

    @Test
    public void testFullDiscountPercentage() {
        dto.setDiscountPercentage(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), dto.getDiscountPercentage());
    }

    @Test
    public void testNegativeDaysUntilExpiry() {
        dto.setDaysUntilExpiry(-5);
        assertEquals(-5, dto.getDaysUntilExpiry());
    }

    @Test
    public void testCompleteProductPromotion() {
        dto.setType("PRODUCT");
        dto.setProductCode("P001");
        dto.setProductName("Product 1");
        dto.setOriginalPrice(new BigDecimal("100.00"));
        dto.setDiscountedPrice(new BigDecimal("80.00"));
        dto.setDiscountPercentage(new BigDecimal("20.00"));
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(30));

        assertNotNull(dto.getType());
        assertNotNull(dto.getProductCode());
        assertNotNull(dto.getOriginalPrice());
    }

    @Test
    public void testNearExpiryFalse() {
        dto.setNearExpiry(false);
        assertFalse(dto.isNearExpiry());
    }
}

