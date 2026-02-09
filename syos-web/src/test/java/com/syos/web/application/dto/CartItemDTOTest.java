package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemDTOTest {

    private CartItemDTO cartItemDTO;

    @BeforeEach
    public void setUp() {
        cartItemDTO = new CartItemDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(cartItemDTO);
    }

    @Test
    public void testFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CartItemDTO item = new CartItemDTO(
            1, "P001", "Product 1", 5,
            new BigDecimal("100.00"), new BigDecimal("120.00"),
            new BigDecimal("16.67"), new BigDecimal("500.00"),
            "image.jpg", true, 10, now
        );

        assertEquals(1, item.getCartId());
        assertEquals("P001", item.getProductCode());
        assertEquals("Product 1", item.getProductName());
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("100.00"), item.getUnitPrice());
        assertTrue(item.isInStock());
    }

    @Test
    public void testSetAndGetCartId() {
        cartItemDTO.setCartId(1);
        assertEquals(1, cartItemDTO.getCartId());
    }

    @Test
    public void testSetAndGetProductCode() {
        cartItemDTO.setProductCode("P001");
        assertEquals("P001", cartItemDTO.getProductCode());
    }

    @Test
    public void testSetAndGetProductName() {
        cartItemDTO.setProductName("Test Product");
        assertEquals("Test Product", cartItemDTO.getProductName());
    }

    @Test
    public void testSetAndGetQuantity() {
        cartItemDTO.setQuantity(5);
        assertEquals(5, cartItemDTO.getQuantity());
    }

    @Test
    public void testSetAndGetUnitPrice() {
        BigDecimal price = new BigDecimal("99.99");
        cartItemDTO.setUnitPrice(price);
        assertEquals(price, cartItemDTO.getUnitPrice());
    }

    @Test
    public void testSetAndGetOriginalPrice() {
        BigDecimal price = new BigDecimal("120.00");
        cartItemDTO.setOriginalPrice(price);
        assertEquals(price, cartItemDTO.getOriginalPrice());
    }

    @Test
    public void testSetAndGetDiscountPercentage() {
        BigDecimal discount = new BigDecimal("25.00");
        cartItemDTO.setDiscountPercentage(discount);
        assertEquals(discount, cartItemDTO.getDiscountPercentage());
    }

    @Test
    public void testSetAndGetSubtotal() {
        BigDecimal subtotal = new BigDecimal("500.00");
        cartItemDTO.setSubtotal(subtotal);
        assertEquals(subtotal, cartItemDTO.getSubtotal());
    }

    @Test
    public void testSetAndGetImageUrl() {
        cartItemDTO.setImageUrl("http://example.com/image.jpg");
        assertEquals("http://example.com/image.jpg", cartItemDTO.getImageUrl());
    }

    @Test
    public void testSetAndGetInStock() {
        cartItemDTO.setInStock(true);
        assertTrue(cartItemDTO.isInStock());

        cartItemDTO.setInStock(false);
        assertFalse(cartItemDTO.isInStock());
    }

    @Test
    public void testSetAndGetAvailableQuantity() {
        cartItemDTO.setAvailableQuantity(100);
        assertEquals(100, cartItemDTO.getAvailableQuantity());
    }

    @Test
    public void testSetAndGetAddedAt() {
        LocalDateTime now = LocalDateTime.now();
        cartItemDTO.setAddedAt(now);
        assertEquals(now, cartItemDTO.getAddedAt());
    }

    @Test
    public void testNullValues() {
        cartItemDTO.setCartId(null);
        cartItemDTO.setProductCode(null);
        cartItemDTO.setProductName(null);
        cartItemDTO.setUnitPrice(null);

        assertNull(cartItemDTO.getCartId());
        assertNull(cartItemDTO.getProductCode());
        assertNull(cartItemDTO.getProductName());
        assertNull(cartItemDTO.getUnitPrice());
    }

    @Test
    public void testZeroQuantity() {
        cartItemDTO.setQuantity(0);
        assertEquals(0, cartItemDTO.getQuantity());
    }

    @Test
    public void testNegativeQuantity() {
        cartItemDTO.setQuantity(-5);
        assertEquals(-5, cartItemDTO.getQuantity());
    }

    @Test
    public void testLargeQuantity() {
        cartItemDTO.setQuantity(10000);
        assertEquals(10000, cartItemDTO.getQuantity());
    }

    @Test
    public void testZeroPrice() {
        cartItemDTO.setUnitPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, cartItemDTO.getUnitPrice());
    }

    @Test
    public void testEmptyStrings() {
        cartItemDTO.setProductCode("");
        cartItemDTO.setProductName("");
        cartItemDTO.setImageUrl("");

        assertEquals("", cartItemDTO.getProductCode());
        assertEquals("", cartItemDTO.getProductName());
        assertEquals("", cartItemDTO.getImageUrl());
    }

    @Test
    public void testWhitespaceStrings() {
        cartItemDTO.setProductCode("   ");
        cartItemDTO.setProductName("  ");

        assertEquals("   ", cartItemDTO.getProductCode());
        assertEquals("  ", cartItemDTO.getProductName());
    }

    @Test
    public void testItemWithDiscount() {
        cartItemDTO.setOriginalPrice(new BigDecimal("100.00"));
        cartItemDTO.setUnitPrice(new BigDecimal("80.00"));
        cartItemDTO.setDiscountPercentage(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("100.00"), cartItemDTO.getOriginalPrice());
        assertEquals(new BigDecimal("80.00"), cartItemDTO.getUnitPrice());
        assertEquals(new BigDecimal("20.00"), cartItemDTO.getDiscountPercentage());
    }

    @Test
    public void testItemWithoutDiscount() {
        cartItemDTO.setOriginalPrice(new BigDecimal("100.00"));
        cartItemDTO.setUnitPrice(new BigDecimal("100.00"));
        cartItemDTO.setDiscountPercentage(BigDecimal.ZERO);

        assertEquals(cartItemDTO.getOriginalPrice(), cartItemDTO.getUnitPrice());
        assertEquals(BigDecimal.ZERO, cartItemDTO.getDiscountPercentage());
    }

    @Test
    public void testInStockItem() {
        cartItemDTO.setInStock(true);
        cartItemDTO.setAvailableQuantity(50);

        assertTrue(cartItemDTO.isInStock());
        assertTrue(cartItemDTO.getAvailableQuantity() > 0);
    }

    @Test
    public void testOutOfStockItem() {
        cartItemDTO.setInStock(false);
        cartItemDTO.setAvailableQuantity(0);

        assertFalse(cartItemDTO.isInStock());
        assertEquals(0, cartItemDTO.getAvailableQuantity());
    }

    @Test
    public void testSubtotalCalculation() {
        cartItemDTO.setQuantity(5);
        cartItemDTO.setUnitPrice(new BigDecimal("100.00"));
        BigDecimal expectedSubtotal = new BigDecimal("500.00");
        cartItemDTO.setSubtotal(expectedSubtotal);

        assertEquals(expectedSubtotal, cartItemDTO.getSubtotal());
    }

    @Test
    public void testSpecialCharactersInProductName() {
        cartItemDTO.setProductName("Product #1 @Special!");
        assertEquals("Product #1 @Special!", cartItemDTO.getProductName());
    }

    @Test
    public void testUnicodeCharacters() {
        cartItemDTO.setProductName("Producto en Español");
        assertEquals("Producto en Español", cartItemDTO.getProductName());
    }

    @Test
    public void testVeryLongProductName() {
        String longName = "A".repeat(500);
        cartItemDTO.setProductName(longName);
        assertEquals(500, cartItemDTO.getProductName().length());
    }

    @Test
    public void testPastAddedAtDate() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        cartItemDTO.setAddedAt(pastDate);
        assertTrue(cartItemDTO.getAddedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    public void testCompleteCartItemScenario() {
        LocalDateTime now = LocalDateTime.now();
        cartItemDTO.setCartId(1);
        cartItemDTO.setProductCode("P001");
        cartItemDTO.setProductName("Laptop");
        cartItemDTO.setQuantity(2);
        cartItemDTO.setOriginalPrice(new BigDecimal("1000.00"));
        cartItemDTO.setUnitPrice(new BigDecimal("900.00"));
        cartItemDTO.setDiscountPercentage(new BigDecimal("10.00"));
        cartItemDTO.setSubtotal(new BigDecimal("1800.00"));
        cartItemDTO.setImageUrl("laptop.jpg");
        cartItemDTO.setInStock(true);
        cartItemDTO.setAvailableQuantity(50);
        cartItemDTO.setAddedAt(now);

        assertNotNull(cartItemDTO.getCartId());
        assertNotNull(cartItemDTO.getProductCode());
        assertNotNull(cartItemDTO.getProductName());
        assertTrue(cartItemDTO.getQuantity() > 0);
        assertTrue(cartItemDTO.isInStock());
    }
}

