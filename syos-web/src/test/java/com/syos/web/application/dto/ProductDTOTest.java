package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDTOTest {

    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        productDTO = new ProductDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(productDTO);
    }

    @Test
    public void testSetAndGetProductCode() {
        productDTO.setProductCode("P001");
        assertEquals("P001", productDTO.getProductCode());
    }

    @Test
    public void testSetAndGetName() {
        productDTO.setName("Product 1");
        assertEquals("Product 1", productDTO.getName());
    }

    @Test
    public void testSetAndGetUnitPrice() {
        BigDecimal price = new BigDecimal("100.00");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testSetAndGetImageUrl() {
        productDTO.setImageUrl("image.jpg");
        assertEquals("image.jpg", productDTO.getImageUrl());
    }

    @Test
    public void testSetAndGetCategoryId() {
        productDTO.setCategoryId(1);
        assertEquals(1, productDTO.getCategoryId());
    }

    @Test
    public void testSetAndGetCategoryName() {
        productDTO.setCategoryName("Electronics");
        assertEquals("Electronics", productDTO.getCategoryName());
    }

    @Test
    public void testSetAndGetDiscountPercentage() {
        BigDecimal discount = new BigDecimal("15.00");
        productDTO.setDiscountPercentage(discount);
        assertEquals(discount, productDTO.getDiscountPercentage());
    }

    @Test
    public void testSetAndGetShelfQuantity() {
        productDTO.setShelfQuantity(50);
        assertEquals(50, productDTO.getShelfQuantity());
    }

    @Test
    public void testSetAndGetWarehouseQuantity() {
        productDTO.setWarehouseQuantity(100);
        assertEquals(100, productDTO.getWarehouseQuantity());
    }

    @Test
    public void testSetAndGetWebsiteQuantity() {
        productDTO.setWebsiteQuantity(75);
        assertEquals(75, productDTO.getWebsiteQuantity());
    }

    @Test
    public void testSetAndGetTotalQuantity() {
        productDTO.setTotalQuantity(225);
        assertEquals(225, productDTO.getTotalQuantity());
    }

    @Test
    public void testSetAndGetStatus() {
        productDTO.setStatus("ACTIVE");
        assertEquals("ACTIVE", productDTO.getStatus());
    }

    @Test
    public void testSetAndGetNeedsReordering() {
        productDTO.setNeedsReordering(true);
        assertTrue(productDTO.isNeedsReordering());
    }

    @Test
    public void testNullValues() {
        productDTO.setProductCode(null);
        productDTO.setName(null);
        productDTO.setUnitPrice(null);

        assertNull(productDTO.getProductCode());
        assertNull(productDTO.getName());
        assertNull(productDTO.getUnitPrice());
    }

    @Test
    public void testEmptyStrings() {
        productDTO.setProductCode("");
        productDTO.setName("");
        productDTO.setImageUrl("");

        assertEquals("", productDTO.getProductCode());
        assertEquals("", productDTO.getName());
        assertEquals("", productDTO.getImageUrl());
    }

    @Test
    public void testZeroQuantities() {
        productDTO.setShelfQuantity(0);
        productDTO.setWarehouseQuantity(0);
        productDTO.setWebsiteQuantity(0);

        assertEquals(0, productDTO.getShelfQuantity());
        assertEquals(0, productDTO.getWarehouseQuantity());
        assertEquals(0, productDTO.getWebsiteQuantity());
    }

    @Test
    public void testNegativeQuantities() {
        productDTO.setShelfQuantity(-5);
        assertEquals(-5, productDTO.getShelfQuantity());
    }

    @Test
    public void testZeroPrice() {
        productDTO.setUnitPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, productDTO.getUnitPrice());
    }

    @Test
    public void testNegativePrice() {
        productDTO.setUnitPrice(new BigDecimal("-10.00"));
        assertEquals(new BigDecimal("-10.00"), productDTO.getUnitPrice());
    }

    @Test
    public void testLargePrice() {
        BigDecimal largePrice = new BigDecimal("9999999.99");
        productDTO.setUnitPrice(largePrice);
        assertEquals(largePrice, productDTO.getUnitPrice());
    }

    @Test
    public void testProductWithDiscount() {
        productDTO.setUnitPrice(new BigDecimal("100.00"));
        productDTO.setDiscountPercentage(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("100.00"), productDTO.getUnitPrice());
        assertEquals(new BigDecimal("20.00"), productDTO.getDiscountPercentage());
    }

    @Test
    public void testProductWithoutDiscount() {
        productDTO.setUnitPrice(new BigDecimal("100.00"));
        productDTO.setDiscountPercentage(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, productDTO.getDiscountPercentage());
    }

    @Test
    public void testNeedsReordering() {
        productDTO.setWarehouseQuantity(45);
        productDTO.setNeedsReordering(true);

        assertTrue(productDTO.isNeedsReordering());
        assertTrue(productDTO.getWarehouseQuantity() < 50);
    }

    @Test
    public void testDoesNotNeedReordering() {
        productDTO.setWarehouseQuantity(100);
        productDTO.setNeedsReordering(false);

        assertFalse(productDTO.isNeedsReordering());
    }

    @Test
    public void testAllStatusTypes() {
        String[] statuses = {"ACTIVE", "INACTIVE", "OUT_OF_STOCK"};
        for (String status : statuses) {
            productDTO.setStatus(status);
            assertEquals(status, productDTO.getStatus());
        }
    }

    @Test
    public void testSpecialCharactersInName() {
        productDTO.setName("Product @#$ Special!");
        assertEquals("Product @#$ Special!", productDTO.getName());
    }

    @Test
    public void testUnicodeCharacters() {
        productDTO.setName("Producto Español");
        productDTO.setCategoryName("Categoría");
        assertEquals("Producto Español", productDTO.getName());
        assertEquals("Categoría", productDTO.getCategoryName());
    }

    @Test
    public void testVeryLongProductName() {
        String longName = "A".repeat(1000);
        productDTO.setName(longName);
        assertEquals(1000, productDTO.getName().length());
    }

    @Test
    public void testCompleteProductScenario() {
        productDTO.setProductCode("P001");
        productDTO.setName("Laptop");
        productDTO.setUnitPrice(new BigDecimal("1000.00"));
        productDTO.setImageUrl("laptop.jpg");
        productDTO.setCategoryId(1);
        productDTO.setCategoryName("Electronics");
        productDTO.setDiscountPercentage(new BigDecimal("10.00"));
        productDTO.setShelfQuantity(10);
        productDTO.setWarehouseQuantity(100);
        productDTO.setWebsiteQuantity(50);
        productDTO.setTotalQuantity(160);
        productDTO.setStatus("ACTIVE");
        productDTO.setNeedsReordering(false);

        assertNotNull(productDTO.getProductCode());
        assertNotNull(productDTO.getName());
        assertNotNull(productDTO.getUnitPrice());
        assertEquals("ACTIVE", productDTO.getStatus());
        assertEquals(160, productDTO.getTotalQuantity());
    }

    @Test
    public void testTotalQuantityCalculation() {
        productDTO.setShelfQuantity(10);
        productDTO.setWarehouseQuantity(20);
        productDTO.setWebsiteQuantity(30);
        productDTO.setTotalQuantity(60);

        assertEquals(60, productDTO.getTotalQuantity());
    }

    @Test
    public void testPriceWithMultipleDecimalPlaces() {
        BigDecimal price = new BigDecimal("99.999");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
        assertEquals(3, productDTO.getUnitPrice().scale());
    }

    @Test
    public void testHighDiscountPercentage() {
        BigDecimal discount = new BigDecimal("90.00");
        productDTO.setDiscountPercentage(discount);
        assertEquals(discount, productDTO.getDiscountPercentage());
    }

    @Test
    public void testZeroDiscountPercentage() {
        productDTO.setDiscountPercentage(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, productDTO.getDiscountPercentage());
    }

    @Test
    public void testNullCategoryId() {
        productDTO.setCategoryId(null);
        assertNull(productDTO.getCategoryId());
    }

    @Test
    public void testLargeCategoryId() {
        productDTO.setCategoryId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, productDTO.getCategoryId());
    }

    @Test
    public void testPriceRounding() {
        BigDecimal price = new BigDecimal("99.999");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testVerySmallPrice() {
        BigDecimal price = new BigDecimal("0.01");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testVeryLargePrice() {
        BigDecimal price = new BigDecimal("999999.99");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testProductCodeWithUnderscore() {
        productDTO.setProductCode("P_001");
        assertEquals("P_001", productDTO.getProductCode());
    }

    @Test
    public void testProductCodeWithDot() {
        productDTO.setProductCode("P.001");
        assertEquals("P.001", productDTO.getProductCode());
    }

    @Test
    public void testNameWithNumbers() {
        productDTO.setName("Product 123");
        assertEquals("Product 123", productDTO.getName());
    }

    @Test
    public void testNameWithParentheses() {
        productDTO.setName("Product (Large)");
        assertEquals("Product (Large)", productDTO.getName());
    }

    @Test
    public void testImageUrlWithQueryParams() {
        productDTO.setImageUrl("http://cdn.com/img.jpg?size=large");
        assertEquals("http://cdn.com/img.jpg?size=large", productDTO.getImageUrl());
    }

    @Test
    public void testImageUrlHTTPS() {
        productDTO.setImageUrl("https://secure.com/image.jpg");
        assertEquals("https://secure.com/image.jpg", productDTO.getImageUrl());
    }

    @Test
    public void testCategoryIdOne() {
        productDTO.setCategoryId(1);
        assertEquals(1, productDTO.getCategoryId());
    }

    @Test
    public void testCategoryIdZero() {
        productDTO.setCategoryId(0);
        assertEquals(0, productDTO.getCategoryId());
    }

    @Test
    public void testCompleteProduct() {
        productDTO.setProductCode("PROD001");
        productDTO.setName("Complete Product");
        productDTO.setUnitPrice(new BigDecimal("199.99"));
        productDTO.setImageUrl("https://cdn.com/prod.jpg");
        productDTO.setCategoryId(5);

        assertNotNull(productDTO.getProductCode());
        assertNotNull(productDTO.getName());
        assertNotNull(productDTO.getUnitPrice());
        assertNotNull(productDTO.getImageUrl());
        assertNotNull(productDTO.getCategoryId());
    }

    @Test
    public void testMultipleProducts() {
        ProductDTO dto1 = new ProductDTO();
        dto1.setProductCode("P001");
        dto1.setName("Product 1");

        ProductDTO dto2 = new ProductDTO();
        dto2.setProductCode("P002");
        dto2.setName("Product 2");

        assertNotEquals(dto1.getProductCode(), dto2.getProductCode());
        assertNotEquals(dto1.getName(), dto2.getName());
    }

    @Test
    public void testProductWithAllNulls() {
        ProductDTO dto = new ProductDTO();
        assertNull(dto.getProductCode());
        assertNull(dto.getName());
        assertNull(dto.getUnitPrice());
        assertNull(dto.getImageUrl());
        assertNull(dto.getCategoryId());
    }

    @Test
    public void testPriceComparison() {
        ProductDTO dto1 = new ProductDTO();
        dto1.setUnitPrice(new BigDecimal("100.00"));

        ProductDTO dto2 = new ProductDTO();
        dto2.setUnitPrice(new BigDecimal("200.00"));

        assertTrue(dto2.getUnitPrice().compareTo(dto1.getUnitPrice()) > 0);
    }

    @Test
    public void testNameTrimming() {
        productDTO.setName(" Product Name ");
        assertEquals(" Product Name ", productDTO.getName());
    }

    @Test
    public void testProductCodeUpperCase() {
        productDTO.setProductCode("PROD001");
        assertEquals("PROD001", productDTO.getProductCode());
    }

    @Test
    public void testProductCodeLowerCase() {
        productDTO.setProductCode("prod001");
        assertEquals("prod001", productDTO.getProductCode());
    }

    @Test
    public void testProductCodeMixedCase() {
        productDTO.setProductCode("Prod001");
        assertEquals("Prod001", productDTO.getProductCode());
    }

    @Test
    public void testNameUpperCase() {
        productDTO.setName("PRODUCT NAME");
        assertEquals("PRODUCT NAME", productDTO.getName());
    }

    @Test
    public void testImageUrlRelative() {
        productDTO.setImageUrl("/images/product.jpg");
        assertEquals("/images/product.jpg", productDTO.getImageUrl());
    }

    @Test
    public void testImageUrlAbsolute() {
        productDTO.setImageUrl("http://example.com/image.jpg");
        assertEquals("http://example.com/image.jpg", productDTO.getImageUrl());
    }

    @Test
    public void testPriceWithTrailingZeros() {
        BigDecimal price = new BigDecimal("100.00");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testPriceWithoutDecimals() {
        BigDecimal price = new BigDecimal("100");
        productDTO.setUnitPrice(price);
        assertEquals(price, productDTO.getUnitPrice());
    }

    @Test
    public void testNegativeCategoryId() {
        productDTO.setCategoryId(-1);
        assertEquals(-1, productDTO.getCategoryId());
    }

    @Test
    public void testProductCodeNumeric() {
        productDTO.setProductCode("12345");
        assertEquals("12345", productDTO.getProductCode());
    }

    @Test
    public void testProductCodeAlphanumeric() {
        productDTO.setProductCode("ABC123");
        assertEquals("ABC123", productDTO.getProductCode());
    }
}


