package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class WishlistItemDTOTest {

    private WishlistItemDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new WishlistItemDTO();
    }

    @Test
    public void testDefaultConstructor() { assertNotNull(dto); }

    @Test
    public void testSetAndGetWishlistId() {
        dto.setWishlistId(1);
        assertEquals(1, dto.getWishlistId());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetProductName() {
        dto.setProductName("Product 1");
        assertEquals("Product 1", dto.getProductName());
    }

    @Test
    public void testSetAndGetImageUrl() {
        dto.setImageUrl("image.jpg");
        assertEquals("image.jpg", dto.getImageUrl());
    }

    @Test
    public void testSetAndGetIsInStock() {
        dto.setInStock(true);
        assertTrue(dto.isInStock());
    }

    @Test
    public void testNullWishlistId() {
        dto.setWishlistId(null);
        assertNull(dto.getWishlistId());
    }

    @Test
    public void testNullProductCode() {
        dto.setProductCode(null);
        assertNull(dto.getProductCode());
    }

    @Test
    public void testEmptyProductCode() {
        dto.setProductCode("");
        assertEquals("", dto.getProductCode());
    }

    @Test
    public void testNullProductName() {
        dto.setProductName(null);
        assertNull(dto.getProductName());
    }

    @Test
    public void testEmptyProductName() {
        dto.setProductName("");
        assertEquals("", dto.getProductName());
    }

    @Test
    public void testWhitespaceProductName() {
        dto.setProductName("   ");
        assertEquals("   ", dto.getProductName());
    }

    @Test
    public void testLongProductName() {
        String longName = "A".repeat(500);
        dto.setProductName(longName);
        assertEquals(500, dto.getProductName().length());
    }

    @Test
    public void testNullImageUrl() {
        dto.setImageUrl(null);
        assertNull(dto.getImageUrl());
    }

    @Test
    public void testEmptyImageUrl() {
        dto.setImageUrl("");
        assertEquals("", dto.getImageUrl());
    }

    @Test
    public void testInStockTrue() {
        dto.setInStock(true);
        assertTrue(dto.isInStock());
    }

    @Test
    public void testInStockFalse() {
        dto.setInStock(false);
        assertFalse(dto.isInStock());
    }

    @Test
    public void testCompleteInStockScenario() {
        dto.setWishlistId(1);
        dto.setProductCode("P001");
        dto.setProductName("Laptop");
        dto.setImageUrl("laptop.jpg");
        dto.setInStock(true);

        assertNotNull(dto.getWishlistId());
        assertNotNull(dto.getProductCode());
        assertTrue(dto.isInStock());
    }

    @Test
    public void testCompleteOutOfStockScenario() {
        dto.setWishlistId(2);
        dto.setProductCode("P002");
        dto.setProductName("Phone");
        dto.setImageUrl("phone.jpg");
        dto.setInStock(false);

        assertNotNull(dto.getWishlistId());
        assertFalse(dto.isInStock());
    }
}

