package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class BillItemDTOTest {

    private BillItemDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new BillItemDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        BillItemDTO item = new BillItemDTO(1L, "P001", "Product 1", 5, new BigDecimal("100.00"), new BigDecimal("500.00"));
        assertEquals(1L, item.getBillItemId());
        assertEquals("P001", item.getProductCode());
        assertEquals("Product 1", item.getProductName());
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("100.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("500.00"), item.getTotalPrice());
    }

    @Test
    public void testSetAndGetBillItemId() {
        dto.setBillItemId(1L);
        assertEquals(1L, dto.getBillItemId());
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
    public void testSetAndGetQuantity() {
        dto.setQuantity(10);
        assertEquals(10, dto.getQuantity());
    }

    @Test
    public void testSetAndGetUnitPrice() {
        BigDecimal price = new BigDecimal("99.99");
        dto.setUnitPrice(price);
        assertEquals(price, dto.getUnitPrice());
    }

    @Test
    public void testSetAndGetTotalPrice() {
        BigDecimal total = new BigDecimal("999.90");
        dto.setTotalPrice(total);
        assertEquals(total, dto.getTotalPrice());
    }

    @Test
    public void testNullBillItemId() {
        dto.setBillItemId(null);
        assertNull(dto.getBillItemId());
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
    public void testZeroQuantity() {
        dto.setQuantity(0);
        assertEquals(0, dto.getQuantity());
    }

    @Test
    public void testNegativeQuantity() {
        dto.setQuantity(-5);
        assertEquals(-5, dto.getQuantity());
    }

    @Test
    public void testLargeQuantity() {
        dto.setQuantity(10000);
        assertEquals(10000, dto.getQuantity());
    }

    @Test
    public void testZeroPrice() {
        dto.setUnitPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, dto.getUnitPrice());
    }

    @Test
    public void testNullPrice() {
        dto.setUnitPrice(null);
        assertNull(dto.getUnitPrice());
    }

    @Test
    public void testCalculatedTotal() {
        dto.setQuantity(5);
        dto.setUnitPrice(new BigDecimal("100.00"));
        dto.setTotalPrice(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), dto.getTotalPrice());
    }

    @Test
    public void testSpecialCharactersInName() {
        dto.setProductName("Product @#$");
        assertEquals("Product @#$", dto.getProductName());
    }

    @Test
    public void testLongProductName() {
        String longName = "A".repeat(500);
        dto.setProductName(longName);
        assertEquals(500, dto.getProductName().length());
    }

    @Test
    public void testCompleteScenario() {
        dto.setBillItemId(1L);
        dto.setProductCode("P001");
        dto.setProductName("Laptop");
        dto.setQuantity(2);
        dto.setUnitPrice(new BigDecimal("1000.00"));
        dto.setTotalPrice(new BigDecimal("2000.00"));

        assertNotNull(dto.getBillItemId());
        assertNotNull(dto.getProductCode());
        assertEquals(2, dto.getQuantity());
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
    public void testNullTotalPrice() {
        dto.setTotalPrice(null);
        assertNull(dto.getTotalPrice());
    }

    @Test
    public void testZeroTotalPrice() {
        dto.setTotalPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, dto.getTotalPrice());
    }

    @Test
    public void testNegativeTotalPrice() {
        dto.setTotalPrice(new BigDecimal("-100.00"));
        assertEquals(new BigDecimal("-100.00"), dto.getTotalPrice());
    }

    @Test
    public void testLargeTotalPrice() {
        dto.setTotalPrice(new BigDecimal("999999.99"));
        assertEquals(new BigDecimal("999999.99"), dto.getTotalPrice());
    }

    @Test
    public void testPrecisePrice() {
        BigDecimal price = new BigDecimal("123.456789");
        dto.setUnitPrice(price);
        assertEquals(price, dto.getUnitPrice());
    }

    @Test
    public void testProductCodeWithDashes() {
        dto.setProductCode("P-001-A");
        assertEquals("P-001-A", dto.getProductCode());
    }

    @Test
    public void testQuantityOne() {
        dto.setQuantity(1);
        assertEquals(1, dto.getQuantity());
    }

    @Test
    public void testMultipleItemsScenario() {
        dto.setBillItemId(5L);
        dto.setProductCode("PROD123");
        dto.setProductName("Multiple Item Test");
        dto.setQuantity(100);
        dto.setUnitPrice(new BigDecimal("25.50"));
        dto.setTotalPrice(new BigDecimal("2550.00"));

        assertEquals(5L, dto.getBillItemId());
        assertEquals("PROD123", dto.getProductCode());
        assertEquals("Multiple Item Test", dto.getProductName());
        assertEquals(100, dto.getQuantity());
        assertEquals(new BigDecimal("25.50"), dto.getUnitPrice());
        assertEquals(new BigDecimal("2550.00"), dto.getTotalPrice());
    }

    @Test
    public void testUnicodeInProductName() {
        dto.setProductName("产品 Продукт");
        assertEquals("产品 Продукт", dto.getProductName());
    }

    @Test
    public void testSmallPrice() {
        dto.setUnitPrice(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), dto.getUnitPrice());
    }

    @Test
    public void testLargeBillItemId() {
        dto.setBillItemId(999999L);
        assertEquals(999999L, dto.getBillItemId());
    }

    @Test
    public void testZeroBillItemId() {
        dto.setBillItemId(0L);
        assertEquals(0L, dto.getBillItemId());
    }

    @Test
    public void testWhitespaceProductCode() {
        dto.setProductCode("   ");
        assertEquals("   ", dto.getProductCode());
    }

    @Test
    public void testWhitespaceProductName() {
        dto.setProductName("   ");
        assertEquals("   ", dto.getProductName());
    }

    @Test
    public void testPreciseTotalPrice() {
        BigDecimal total = new BigDecimal("987.654321");
        dto.setTotalPrice(total);
        assertEquals(total, dto.getTotalPrice());
    }
}
