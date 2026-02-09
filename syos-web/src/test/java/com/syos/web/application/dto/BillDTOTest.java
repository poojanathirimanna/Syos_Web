package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillDTOTest {

    private BillDTO billDTO;

    @BeforeEach
    public void setUp() {
        billDTO = new BillDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(billDTO);
    }

    @Test
    public void testSetAndGetBillNumber() {
        billDTO.setBillNumber("B001");
        assertEquals("B001", billDTO.getBillNumber());
    }

    @Test
    public void testSetAndGetBillDate() {
        LocalDateTime date = LocalDateTime.now();
        billDTO.setBillDate(date);
        assertEquals(date, billDTO.getBillDate());
    }

    @Test
    public void testSetAndGetUserId() {
        billDTO.setUserId("U001");
        assertEquals("U001", billDTO.getUserId());
    }

    @Test
    public void testSetAndGetCashierName() {
        billDTO.setCashierName("John Doe");
        assertEquals("John Doe", billDTO.getCashierName());
    }

    @Test
    public void testSetAndGetPaymentMethod() {
        billDTO.setPaymentMethod("CASH");
        assertEquals("CASH", billDTO.getPaymentMethod());
    }

    @Test
    public void testSetAndGetSubtotal() {
        BigDecimal subtotal = new BigDecimal("100.00");
        billDTO.setSubtotal(subtotal);
        assertEquals(subtotal, billDTO.getSubtotal());
    }

    @Test
    public void testSetAndGetDiscountAmount() {
        BigDecimal discount = new BigDecimal("10.00");
        billDTO.setDiscountAmount(discount);
        assertEquals(discount, billDTO.getDiscountAmount());
    }

    @Test
    public void testSetAndGetTotalAmount() {
        BigDecimal total = new BigDecimal("90.00");
        billDTO.setTotalAmount(total);
        assertEquals(total, billDTO.getTotalAmount());
    }

    @Test
    public void testSetAndGetAmountPaid() {
        BigDecimal paid = new BigDecimal("100.00");
        billDTO.setAmountPaid(paid);
        assertEquals(paid, billDTO.getAmountPaid());
    }

    @Test
    public void testSetAndGetChangeAmount() {
        BigDecimal change = new BigDecimal("10.00");
        billDTO.setChangeAmount(change);
        assertEquals(change, billDTO.getChangeAmount());
    }

    @Test
    public void testSetAndGetItems() {
        List<BillItemDTO> items = new ArrayList<>();
        BillItemDTO item = new BillItemDTO();
        items.add(item);
        billDTO.setItems(items);
        assertEquals(items, billDTO.getItems());
        assertEquals(1, billDTO.getItems().size());
    }

    @Test
    public void testSetAndGetChannel() {
        billDTO.setChannel("IN_STORE");
        assertEquals("IN_STORE", billDTO.getChannel());
    }

    @Test
    public void testSetAndGetOrderStatus() {
        billDTO.setOrderStatus("PENDING");
        assertEquals("PENDING", billDTO.getOrderStatus());
    }

    @Test
    public void testSetAndGetPaymentStatus() {
        billDTO.setPaymentStatus("PAID");
        assertEquals("PAID", billDTO.getPaymentStatus());
    }

    @Test
    public void testSetAndGetTrackingNumber() {
        billDTO.setTrackingNumber("TRK123456");
        assertEquals("TRK123456", billDTO.getTrackingNumber());
    }

    @Test
    public void testSetAndGetEstimatedDeliveryDate() {
        LocalDate deliveryDate = LocalDate.now().plusDays(5);
        billDTO.setEstimatedDeliveryDate(deliveryDate);
        assertEquals(deliveryDate, billDTO.getEstimatedDeliveryDate());
    }

    @Test
    public void testSetAndGetDeliveryAddress() {
        billDTO.setDeliveryAddress("123 Main St");
        assertEquals("123 Main St", billDTO.getDeliveryAddress());
    }

    @Test
    public void testSetAndGetDeliveryCity() {
        billDTO.setDeliveryCity("Colombo");
        assertEquals("Colombo", billDTO.getDeliveryCity());
    }

    @Test
    public void testSetAndGetDeliveryPostalCode() {
        billDTO.setDeliveryPostalCode("10100");
        assertEquals("10100", billDTO.getDeliveryPostalCode());
    }

    @Test
    public void testSetAndGetDeliveryPhone() {
        billDTO.setDeliveryPhone("0771234567");
        assertEquals("0771234567", billDTO.getDeliveryPhone());
    }

    @Test
    public void testNullBillNumber() {
        billDTO.setBillNumber(null);
        assertNull(billDTO.getBillNumber());
    }

    @Test
    public void testNullBillDate() {
        billDTO.setBillDate(null);
        assertNull(billDTO.getBillDate());
    }

    @Test
    public void testNullUserId() {
        billDTO.setUserId(null);
        assertNull(billDTO.getUserId());
    }

    @Test
    public void testNullPaymentMethod() {
        billDTO.setPaymentMethod(null);
        assertNull(billDTO.getPaymentMethod());
    }

    @Test
    public void testNullSubtotal() {
        billDTO.setSubtotal(null);
        assertNull(billDTO.getSubtotal());
    }

    @Test
    public void testNullDiscountAmount() {
        billDTO.setDiscountAmount(null);
        assertNull(billDTO.getDiscountAmount());
    }

    @Test
    public void testNullTotalAmount() {
        billDTO.setTotalAmount(null);
        assertNull(billDTO.getTotalAmount());
    }

    @Test
    public void testZeroAmounts() {
        billDTO.setSubtotal(BigDecimal.ZERO);
        billDTO.setDiscountAmount(BigDecimal.ZERO);
        billDTO.setTotalAmount(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, billDTO.getSubtotal());
        assertEquals(BigDecimal.ZERO, billDTO.getDiscountAmount());
        assertEquals(BigDecimal.ZERO, billDTO.getTotalAmount());
    }

    @Test
    public void testLargeAmounts() {
        BigDecimal largeAmount = new BigDecimal("999999.99");
        billDTO.setSubtotal(largeAmount);
        assertEquals(largeAmount, billDTO.getSubtotal());
    }

    @Test
    public void testEmptyItems() {
        billDTO.setItems(new ArrayList<>());
        assertNotNull(billDTO.getItems());
        assertTrue(billDTO.getItems().isEmpty());
    }

    @Test
    public void testMultipleItems() {
        List<BillItemDTO> items = Arrays.asList(
            new BillItemDTO(),
            new BillItemDTO(),
            new BillItemDTO()
        );
        billDTO.setItems(items);
        assertEquals(3, billDTO.getItems().size());
    }

    @Test
    public void testInStoreBill() {
        billDTO.setChannel("IN_STORE");
        billDTO.setOrderStatus(null);
        billDTO.setPaymentStatus(null);
        assertEquals("IN_STORE", billDTO.getChannel());
        assertNull(billDTO.getOrderStatus());
        assertNull(billDTO.getPaymentStatus());
    }

    @Test
    public void testOnlineOrderBill() {
        billDTO.setChannel("ONLINE");
        billDTO.setOrderStatus("PENDING");
        billDTO.setPaymentStatus("PENDING");
        billDTO.setTrackingNumber("TRK789");
        billDTO.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));

        assertEquals("ONLINE", billDTO.getChannel());
        assertEquals("PENDING", billDTO.getOrderStatus());
        assertEquals("PENDING", billDTO.getPaymentStatus());
        assertEquals("TRK789", billDTO.getTrackingNumber());
        assertNotNull(billDTO.getEstimatedDeliveryDate());
    }

    @Test
    public void testEmptyStrings() {
        billDTO.setBillNumber("");
        billDTO.setCashierName("");
        billDTO.setPaymentMethod("");
        assertEquals("", billDTO.getBillNumber());
        assertEquals("", billDTO.getCashierName());
        assertEquals("", billDTO.getPaymentMethod());
    }

    @Test
    public void testWhitespaceStrings() {
        billDTO.setBillNumber("   ");
        billDTO.setChannel("  ");
        assertEquals("   ", billDTO.getBillNumber());
        assertEquals("  ", billDTO.getChannel());
    }

    @Test
    public void testNegativeAmounts() {
        BigDecimal negative = new BigDecimal("-10.00");
        billDTO.setSubtotal(negative);
        assertEquals(negative, billDTO.getSubtotal());
    }

    @Test
    public void testPastBillDate() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(10);
        billDTO.setBillDate(pastDate);
        assertTrue(billDTO.getBillDate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void testFutureBillDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
        billDTO.setBillDate(futureDate);
        assertTrue(billDTO.getBillDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testPastDeliveryDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        billDTO.setEstimatedDeliveryDate(pastDate);
        assertTrue(billDTO.getEstimatedDeliveryDate().isBefore(LocalDate.now()));
    }

    @Test
    public void testFutureDeliveryDate() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        billDTO.setEstimatedDeliveryDate(futureDate);
        assertTrue(billDTO.getEstimatedDeliveryDate().isAfter(LocalDate.now()));
    }

    @Test
    public void testSpecialCharactersInStrings() {
        billDTO.setBillNumber("B@#$%001");
        billDTO.setCashierName("John@Doe");
        billDTO.setDeliveryAddress("123 Main St., Apt #5");
        assertEquals("B@#$%001", billDTO.getBillNumber());
        assertEquals("John@Doe", billDTO.getCashierName());
        assertEquals("123 Main St., Apt #5", billDTO.getDeliveryAddress());
    }

    @Test
    public void testUnicodeCharacters() {
        billDTO.setCashierName("José García");
        billDTO.setDeliveryCity("São Paulo");
        assertEquals("José García", billDTO.getCashierName());
        assertEquals("São Paulo", billDTO.getDeliveryCity());
    }

    @Test
    public void testVeryLongStrings() {
        String longString = "A".repeat(1000);
        billDTO.setDeliveryAddress(longString);
        assertEquals(1000, billDTO.getDeliveryAddress().length());
    }

    @Test
    public void testAllPaymentMethods() {
        String[] paymentMethods = {"CASH", "CARD", "DIGITAL", "BANK_TRANSFER"};
        for (String method : paymentMethods) {
            billDTO.setPaymentMethod(method);
            assertEquals(method, billDTO.getPaymentMethod());
        }
    }

    @Test
    public void testAllOrderStatuses() {
        String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        for (String status : statuses) {
            billDTO.setOrderStatus(status);
            assertEquals(status, billDTO.getOrderStatus());
        }
    }

    @Test
    public void testAllPaymentStatuses() {
        String[] statuses = {"PENDING", "PAID", "FAILED"};
        for (String status : statuses) {
            billDTO.setPaymentStatus(status);
            assertEquals(status, billDTO.getPaymentStatus());
        }
    }

    @Test
    public void testPrecisionOfBigDecimal() {
        BigDecimal preciseAmount = new BigDecimal("99.999");
        billDTO.setSubtotal(preciseAmount);
        assertEquals(preciseAmount, billDTO.getSubtotal());
        assertEquals(3, billDTO.getSubtotal().scale());
    }

    @Test
    public void testCompleteInStoreBillScenario() {
        billDTO.setBillNumber("B001");
        billDTO.setBillDate(LocalDateTime.now());
        billDTO.setUserId("U001");
        billDTO.setCashierName("Jane Doe");
        billDTO.setPaymentMethod("CASH");
        billDTO.setSubtotal(new BigDecimal("200.00"));
        billDTO.setDiscountAmount(new BigDecimal("20.00"));
        billDTO.setTotalAmount(new BigDecimal("180.00"));
        billDTO.setAmountPaid(new BigDecimal("200.00"));
        billDTO.setChangeAmount(new BigDecimal("20.00"));
        billDTO.setChannel("IN_STORE");

        assertNotNull(billDTO.getBillNumber());
        assertNotNull(billDTO.getBillDate());
        assertNotNull(billDTO.getUserId());
        assertNotNull(billDTO.getCashierName());
        assertEquals("CASH", billDTO.getPaymentMethod());
        assertEquals("IN_STORE", billDTO.getChannel());
    }

    @Test
    public void testCompleteOnlineOrderScenario() {
        billDTO.setBillNumber("B002");
        billDTO.setBillDate(LocalDateTime.now());
        billDTO.setUserId("C001");
        billDTO.setPaymentMethod("CARD");
        billDTO.setSubtotal(new BigDecimal("500.00"));
        billDTO.setDiscountAmount(new BigDecimal("50.00"));
        billDTO.setTotalAmount(new BigDecimal("450.00"));
        billDTO.setAmountPaid(new BigDecimal("450.00"));
        billDTO.setChannel("ONLINE");
        billDTO.setOrderStatus("PENDING");
        billDTO.setPaymentStatus("PAID");
        billDTO.setTrackingNumber("TRK20260209001");
        billDTO.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));
        billDTO.setDeliveryAddress("456 Park Ave");
        billDTO.setDeliveryCity("Colombo");
        billDTO.setDeliveryPostalCode("10200");
        billDTO.setDeliveryPhone("0771234567");

        assertEquals("ONLINE", billDTO.getChannel());
        assertEquals("PENDING", billDTO.getOrderStatus());
        assertEquals("PAID", billDTO.getPaymentStatus());
        assertNotNull(billDTO.getTrackingNumber());
        assertNotNull(billDTO.getEstimatedDeliveryDate());
        assertNotNull(billDTO.getDeliveryAddress());
    }
}

