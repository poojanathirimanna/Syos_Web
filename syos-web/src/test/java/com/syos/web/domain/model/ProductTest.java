package com.syos.web.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(product);
    }

    @Test
    public void testSetAndGetProductCode() {
        product.setProductCode("P001");
        assertEquals("P001", product.getProductCode());
    }

    @Test
    public void testSetAndGetName() {
        product.setName("Product 1");
        assertEquals("Product 1", product.getName());
    }

    @Test
    public void testSetAndGetUnitPrice() {
        BigDecimal price = new BigDecimal("100.00");
        product.setUnitPrice(price);
        assertEquals(price, product.getUnitPrice());
    }

    @Test
    public void testSetAndGetImageUrl() {
        product.setImageUrl("image.jpg");
        assertEquals("image.jpg", product.getImageUrl());
    }

    @Test
    public void testSetAndGetCategoryId() {
        product.setCategoryId(1);
        assertEquals(1, product.getCategoryId());
    }

    @Test
    public void testSetAndGetShelfQuantity() {
        product.setShelfQuantity(50);
        assertEquals(50, product.getShelfQuantity());
    }

    @Test
    public void testSetAndGetWarehouseQuantity() {
        product.setWarehouseQuantity(100);
        assertEquals(100, product.getWarehouseQuantity());
    }

    @Test
    public void testSetAndGetWebsiteQuantity() {
        product.setWebsiteQuantity(75);
        assertEquals(75, product.getWebsiteQuantity());
    }

    @Test
    public void testSetAndGetDeleted() {
        product.setDeleted(true);
        assertTrue(product.isDeleted());
    }

    @Test
    public void testHasActiveDiscountTrue() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(LocalDate.now().minusDays(1));
        product.setDiscountEndDate(LocalDate.now().plusDays(1));

        assertTrue(product.hasActiveDiscount());
    }

    @Test
    public void testHasActiveDiscountFalseNoDiscount() {
        product.setDiscountPercentage(BigDecimal.ZERO);

        assertFalse(product.hasActiveDiscount());
    }

    @Test
    public void testHasActiveDiscountFalseExpired() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(LocalDate.now().minusDays(10));
        product.setDiscountEndDate(LocalDate.now().minusDays(1));

        assertFalse(product.hasActiveDiscount());
    }

    @Test
    public void testHasActiveDiscountFalseNotStarted() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(LocalDate.now().plusDays(1));
        product.setDiscountEndDate(LocalDate.now().plusDays(10));

        assertFalse(product.hasActiveDiscount());
    }

    @Test
    public void testGetDiscountedPrice() {
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setDiscountPercentage(new BigDecimal("20.00"));

        BigDecimal discountedPrice = product.getDiscountedPrice();
        assertEquals(new BigDecimal("80.00"), discountedPrice);
    }

    @Test
    public void testGetDiscountedPriceNoDiscount() {
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setDiscountPercentage(BigDecimal.ZERO);

        BigDecimal discountedPrice = product.getDiscountedPrice();
        assertEquals(new BigDecimal("100.00"), discountedPrice);
    }

    @Test
    public void testGetDiscountedPriceHighDiscount() {
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setDiscountPercentage(new BigDecimal("90.00"));

        BigDecimal discountedPrice = product.getDiscountedPrice();
        assertEquals(new BigDecimal("10.00"), discountedPrice);
    }

    @Test
    public void testNullValues() {
        product.setProductCode(null);
        product.setName(null);
        product.setUnitPrice(null);

        assertNull(product.getProductCode());
        assertNull(product.getName());
        assertNull(product.getUnitPrice());
    }

    @Test
    public void testZeroQuantities() {
        product.setShelfQuantity(0);
        product.setWarehouseQuantity(0);
        product.setWebsiteQuantity(0);

        assertEquals(0, product.getShelfQuantity());
        assertEquals(0, product.getWarehouseQuantity());
        assertEquals(0, product.getWebsiteQuantity());
    }

    @Test
    public void testNegativeQuantities() {
        product.setShelfQuantity(-5);
        product.setWarehouseQuantity(-10);
        product.setWebsiteQuantity(-3);

        assertEquals(-5, product.getShelfQuantity());
        assertEquals(-10, product.getWarehouseQuantity());
        assertEquals(-3, product.getWebsiteQuantity());
    }

    @Test
    public void testLargeQuantities() {
        product.setShelfQuantity(10000);
        product.setWarehouseQuantity(50000);
        product.setWebsiteQuantity(25000);

        assertEquals(10000, product.getShelfQuantity());
        assertEquals(50000, product.getWarehouseQuantity());
        assertEquals(25000, product.getWebsiteQuantity());
    }

    @Test
    public void testEmptyStrings() {
        product.setProductCode("");
        product.setName("");
        product.setImageUrl("");

        assertEquals("", product.getProductCode());
        assertEquals("", product.getName());
        assertEquals("", product.getImageUrl());
    }

    @Test
    public void testWhitespaceStrings() {
        product.setProductCode("   ");
        product.setName("  ");

        assertEquals("   ", product.getProductCode());
        assertEquals("  ", product.getName());
    }

    @Test
    public void testSpecialCharacters() {
        product.setProductCode("P@001");
        product.setName("Product #1 & Special!");

        assertEquals("P@001", product.getProductCode());
        assertEquals("Product #1 & Special!", product.getName());
    }

    @Test
    public void testUnicodeCharacters() {
        product.setName("Producto Español 中文");
        assertEquals("Producto Español 中文", product.getName());
    }

    @Test
    public void testVeryLongName() {
        String longName = "A".repeat(1000);
        product.setName(longName);
        assertEquals(1000, product.getName().length());
    }

    @Test
    public void testDeletedFalse() {
        product.setDeleted(false);
        assertFalse(product.isDeleted());
    }

    @Test
    public void testDiscountStartDateToday() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(LocalDate.now());
        product.setDiscountEndDate(LocalDate.now().plusDays(5));

        assertTrue(product.hasActiveDiscount());
    }

    @Test
    public void testDiscountEndDateToday() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(LocalDate.now().minusDays(5));
        product.setDiscountEndDate(LocalDate.now());

        assertTrue(product.hasActiveDiscount());
    }

    @Test
    public void testNullDiscountDates() {
        product.setDiscountPercentage(new BigDecimal("10.00"));
        product.setDiscountStartDate(null);
        product.setDiscountEndDate(null);

        assertFalse(product.hasActiveDiscount());
    }

    @Test
    public void testPriceWithManyDecimalPlaces() {
        BigDecimal price = new BigDecimal("99.99999");
        product.setUnitPrice(price);
        assertEquals(price, product.getUnitPrice());
    }

    @Test
    public void testDiscountCalculationPrecision() {
        product.setUnitPrice(new BigDecimal("99.99"));
        product.setDiscountPercentage(new BigDecimal("15.50"));

        BigDecimal discountedPrice = product.getDiscountedPrice();
        assertNotNull(discountedPrice);
        assertTrue(discountedPrice.compareTo(product.getUnitPrice()) < 0);
    }

    @Test
    public void testCompleteProductScenario() {
        product.setProductCode("P001");
        product.setName("Laptop");
        product.setUnitPrice(new BigDecimal("1000.00"));
        product.setImageUrl("laptop.jpg");
        product.setCategoryId(1);
        product.setShelfQuantity(10);
        product.setWarehouseQuantity(100);
        product.setWebsiteQuantity(50);
        product.setDeleted(false);
        product.setDiscountPercentage(new BigDecimal("15.00"));
        product.setDiscountStartDate(LocalDate.now());
        product.setDiscountEndDate(LocalDate.now().plusDays(7));

        assertNotNull(product.getProductCode());
        assertNotNull(product.getName());
        assertTrue(product.hasActiveDiscount());
        assertFalse(product.isDeleted());
        assertEquals(new BigDecimal("850.00"), product.getDiscountedPrice());
    }
}

