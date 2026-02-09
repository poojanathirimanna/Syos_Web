package com.syos.web.application.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CartSummaryDTOTest {

    private CartSummaryDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new CartSummaryDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
        assertEquals(0, dto.getItemCount());
        assertEquals(BigDecimal.ZERO, dto.getSubtotal());
        assertEquals(BigDecimal.ZERO, dto.getTotalDiscount());
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
    }

    @Test
    public void testFullConstructor() {
        BigDecimal subtotal = new BigDecimal("100.00");
        BigDecimal discount = new BigDecimal("10.00");
        BigDecimal total = new BigDecimal("90.00");

        CartSummaryDTO fullDto = new CartSummaryDTO(5, subtotal, discount, total);

        assertEquals(5, fullDto.getItemCount());
        assertEquals(subtotal, fullDto.getSubtotal());
        assertEquals(discount, fullDto.getTotalDiscount());
        assertEquals(total, fullDto.getTotalAmount());
    }

    @Test
    public void testSetAndGetItemCount() {
        dto.setItemCount(5);
        assertEquals(5, dto.getItemCount());
    }

    @Test
    public void testSetAndGetSubtotal() {
        BigDecimal subtotal = new BigDecimal("150.50");
        dto.setSubtotal(subtotal);
        assertEquals(subtotal, dto.getSubtotal());
    }

    @Test
    public void testSetAndGetTotalDiscount() {
        BigDecimal discount = new BigDecimal("25.00");
        dto.setTotalDiscount(discount);
        assertEquals(discount, dto.getTotalDiscount());
    }

    @Test
    public void testSetAndGetTotalAmount() {
        BigDecimal total = new BigDecimal("125.50");
        dto.setTotalAmount(total);
        assertEquals(total, dto.getTotalAmount());
    }

    @Test
    public void testItemCountZero() {
        dto.setItemCount(0);
        assertEquals(0, dto.getItemCount());
    }

    @Test
    public void testItemCountNegative() {
        dto.setItemCount(-1);
        assertEquals(-1, dto.getItemCount());
    }

    @Test
    public void testItemCountLarge() {
        dto.setItemCount(1000);
        assertEquals(1000, dto.getItemCount());
    }

    @Test
    public void testSubtotalNull() {
        dto.setSubtotal(null);
        assertNull(dto.getSubtotal());
    }

    @Test
    public void testSubtotalZero() {
        dto.setSubtotal(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, dto.getSubtotal());
    }

    @Test
    public void testTotalDiscountNull() {
        dto.setTotalDiscount(null);
        assertNull(dto.getTotalDiscount());
    }

    @Test
    public void testTotalAmountNull() {
        dto.setTotalAmount(null);
        assertNull(dto.getTotalAmount());
    }

    @Test
    public void testLargeSubtotal() {
        BigDecimal large = new BigDecimal("999999.99");
        dto.setSubtotal(large);
        assertEquals(large, dto.getSubtotal());
    }

    @Test
    public void testSmallSubtotal() {
        BigDecimal small = new BigDecimal("0.01");
        dto.setSubtotal(small);
        assertEquals(small, dto.getSubtotal());
    }

    @Test
    public void testCompleteCartScenario() {
        dto.setItemCount(5);
        dto.setSubtotal(new BigDecimal("500.00"));
        dto.setTotalDiscount(new BigDecimal("50.00"));
        dto.setTotalAmount(new BigDecimal("450.00"));

        assertEquals(5, dto.getItemCount());
        assertNotNull(dto.getSubtotal());
        assertNotNull(dto.getTotalDiscount());
        assertNotNull(dto.getTotalAmount());
    }

    @Test
    public void testEmptyCart() {
        dto.setItemCount(0);
        dto.setSubtotal(BigDecimal.ZERO);
        dto.setTotalDiscount(BigDecimal.ZERO);
        dto.setTotalAmount(BigDecimal.ZERO);

        assertEquals(0, dto.getItemCount());
        assertEquals(BigDecimal.ZERO, dto.getSubtotal());
        assertEquals(BigDecimal.ZERO, dto.getTotalDiscount());
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
    }

    @Test
    public void testDiscountGreaterThanSubtotal() {
        dto.setSubtotal(new BigDecimal("100.00"));
        dto.setTotalDiscount(new BigDecimal("150.00"));
        dto.setTotalAmount(new BigDecimal("-50.00"));

        assertEquals(new BigDecimal("-50.00"), dto.getTotalAmount());
    }

    @Test
    public void testPrecision() {
        BigDecimal precise = new BigDecimal("123.456789");
        dto.setSubtotal(precise);
        assertEquals(precise, dto.getSubtotal());
    }
}

