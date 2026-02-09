package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateProductRequestTest {

    private UpdateProductRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new UpdateProductRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testConstructorWithBasicFields() {
        UpdateProductRequest request = new UpdateProductRequest("P001", "Product 1", new BigDecimal("100.00"));
        assertEquals("P001", request.getProductCode());
        assertEquals("Product 1", request.getName());
        assertEquals(new BigDecimal("100.00"), request.getUnitPrice());
    }

    @Test
    public void testConstructorWithImage() {
        UpdateProductRequest request = new UpdateProductRequest("P002", "Product 2", new BigDecimal("50.00"), "http://image.url");
        assertEquals("http://image.url", request.getImageUrl());
    }

    @Test
    public void testConstructorWithAllFields() {
        UpdateProductRequest request = new UpdateProductRequest("P003", "Product 3", new BigDecimal("75.00"), "http://img.url", 1);
        assertEquals(1, request.getCategoryId());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetName() {
        dto.setName("Test Product");
        assertEquals("Test Product", dto.getName());
    }

    @Test
    public void testSetAndGetUnitPrice() {
        BigDecimal price = new BigDecimal("99.99");
        dto.setUnitPrice(price);
        assertEquals(price, dto.getUnitPrice());
    }

    @Test
    public void testSetAndGetImageUrl() {
        dto.setImageUrl("http://test.com/image.jpg");
        assertEquals("http://test.com/image.jpg", dto.getImageUrl());
    }

    @Test
    public void testSetAndGetCategoryId() {
        dto.setCategoryId(5);
        assertEquals(5, dto.getCategoryId());
    }

    @Test
    public void testValidateSuccess() {
        dto.setProductCode("P001");
        dto.setName("Valid Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        dto.setProductCode(null);
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        dto.setProductCode("");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        dto.setProductCode("   ");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullName() {
        dto.setProductCode("P001");
        dto.setName(null);
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyName() {
        dto.setProductCode("P001");
        dto.setName("");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWhitespaceName() {
        dto.setProductCode("P001");
        dto.setName("   ");
        dto.setUnitPrice(new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullUnitPrice() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateZeroUnitPrice() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeUnitPrice() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("-10.00"));
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateInvalidCategoryId() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        dto.setCategoryId(0);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeCategoryId() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        dto.setCategoryId(-1);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWithNullCategoryId() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        dto.setCategoryId(null);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWithNullImageUrl() {
        dto.setProductCode("P001");
        dto.setName("Product");
        dto.setUnitPrice(new BigDecimal("100.00"));
        dto.setImageUrl(null);
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testNullValues() {
        dto.setProductCode(null);
        dto.setName(null);
        dto.setUnitPrice(null);
        dto.setImageUrl(null);
        dto.setCategoryId(null);

        assertNull(dto.getProductCode());
        assertNull(dto.getName());
        assertNull(dto.getUnitPrice());
        assertNull(dto.getImageUrl());
        assertNull(dto.getCategoryId());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        dto.setProductCode(longCode);
        assertEquals(longCode, dto.getProductCode());
    }

    @Test
    public void testLongProductName() {
        String longName = "A".repeat(500);
        dto.setName(longName);
        assertEquals(500, dto.getName().length());
    }

    @Test
    public void testSpecialCharsInName() {
        dto.setName("Product & Items (50%) - Special!");
        assertEquals("Product & Items (50%) - Special!", dto.getName());
    }

    @Test
    public void testUnicodeInName() {
        dto.setName("Product 中文 Español");
        assertEquals("Product 中文 Español", dto.getName());
    }

    @Test
    public void testSmallUnitPrice() {
        dto.setUnitPrice(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), dto.getUnitPrice());
    }

    @Test
    public void testLargeUnitPrice() {
        dto.setUnitPrice(new BigDecimal("999999.99"));
        assertEquals(new BigDecimal("999999.99"), dto.getUnitPrice());
    }

    @Test
    public void testPreciseUnitPrice() {
        dto.setUnitPrice(new BigDecimal("123.456789"));
        assertEquals(new BigDecimal("123.456789"), dto.getUnitPrice());
    }

    @Test
    public void testImageUrlWithHTTPS() {
        dto.setImageUrl("https://secure.com/image.jpg");
        assertEquals("https://secure.com/image.jpg", dto.getImageUrl());
    }

    @Test
    public void testImageUrlWithQueryParams() {
        dto.setImageUrl("http://cdn.com/img.jpg?size=large");
        assertEquals("http://cdn.com/img.jpg?size=large", dto.getImageUrl());
    }

    @Test
    public void testEmptyImageUrl() {
        dto.setImageUrl("");
        assertEquals("", dto.getImageUrl());
    }

    @Test
    public void testCategoryIdOne() {
        dto.setCategoryId(1);
        assertEquals(1, dto.getCategoryId());
    }

    @Test
    public void testCategoryIdLarge() {
        dto.setCategoryId(99999);
        assertEquals(99999, dto.getCategoryId());
    }

    @Test
    public void testCompleteUpdate() {
        dto.setProductCode("PROD001");
        dto.setName("Updated Product");
        dto.setUnitPrice(new BigDecimal("199.99"));
        dto.setImageUrl("https://cdn.example.com/product.jpg");
        dto.setCategoryId(3);

        assertNotNull(dto.getProductCode());
        assertNotNull(dto.getName());
        assertNotNull(dto.getUnitPrice());
        assertNotNull(dto.getImageUrl());
        assertNotNull(dto.getCategoryId());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testMinimalUpdate() {
        dto.setProductCode("MIN");
        dto.setName("M");
        dto.setUnitPrice(new BigDecimal("0.01"));
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testProductCodeWithSpecialChars() {
        dto.setProductCode("P-001_A");
        assertEquals("P-001_A", dto.getProductCode());
    }

    @Test
    public void testNameWithNumbers() {
        dto.setName("Product 12345");
        assertEquals("Product 12345", dto.getName());
    }

    @Test
    public void testUnitPriceRounding() {
        BigDecimal price = new BigDecimal("99.999");
        dto.setUnitPrice(price);
        assertEquals(price, dto.getUnitPrice());
    }
}

