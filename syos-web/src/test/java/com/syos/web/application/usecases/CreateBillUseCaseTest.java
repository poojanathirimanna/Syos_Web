package com.syos.web.application.usecases;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.domain.model.Product;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateBillUseCaseTest {

    @Mock
    private BillDao billDao;

    @Mock
    private ProductDao productDao;

    private CreateBillUseCase createBillUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        createBillUseCase = new CreateBillUseCase(billDao, productDao);
    }

    @Test
    public void testExecuteSuccessCashier() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(2);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("200"));

        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Product 1");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(billDao).createBill(eq("U001"), eq("CASHIER"), eq("IN_STORE"), eq("CASH"), any(), any(), any(), eq(new BigDecimal("200")), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteSuccessOnline() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(2);
        request.setItems(Collections.singletonList(item));
        request.setChannel("ONLINE");
        request.setPaymentMethod("CARD");
        request.setDeliveryAddress("123 Street");
        request.setDeliveryCity("City");
        request.setDeliveryPostalCode("12345");
        request.setDeliveryPhone("1234567890");

        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Product 1");
        product.setUnitPrice(new BigDecimal("100"));
        product.setWebsiteQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), eq("123 Street"), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001", "CUSTOMER");

        assertNotNull(result);
        verify(billDao).createBill(eq("U001"), eq("CUSTOMER"), eq("ONLINE"), eq("CARD"), any(), any(), any(), any(), any(), eq("123 Street"), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteInsufficientStock() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(20);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");

        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Product 1");
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request, "U001"));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    public void testExecuteProductNotFound() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P999");
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");

        when(productDao.findByProductCode("P999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request, "U001"));
        assertEquals("Product not found: P999", exception.getMessage());
    }

    @Test
    public void testExecuteAmountPaidLessThanTotal() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("50"));

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request, "U001"));
        assertTrue(exception.getMessage().contains("Amount paid"));
    }
    
    @Test
    public void testExecuteWithDiscount() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(2);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("200"));

        Product product = mock(Product.class);
        when(product.getProductCode()).thenReturn("P001");
        when(product.getName()).thenReturn("Product 1");
        when(product.getUnitPrice()).thenReturn(new BigDecimal("100"));
        when(product.getShelfQuantity()).thenReturn(10);
        when(product.isDeleted()).thenReturn(false);
        when(product.hasActiveDiscount()).thenReturn(true);
        when(product.getDiscountedPrice()).thenReturn(new BigDecimal("80"));
        when(product.getDiscountPercentage()).thenReturn(new BigDecimal("20"));

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(billDao).createBill(anyString(), anyString(), anyString(), anyString(), eq(new BigDecimal("160")), eq(new BigDecimal("40")), eq(new BigDecimal("160")), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteSQLException() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(new SQLException("DB Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> createBillUseCase.execute(request, "U001"));
        assertTrue(exception.getMessage().contains("Failed to create bill"));
    }

    @Test
    public void testExecuteDeletedProduct() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");

        Product product = new Product();
        product.setProductCode("P001");
        product.setDeleted(true);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request, "U001"));
        assertTrue(exception.getMessage().contains("no longer available"));
    }

    @Test
    public void testExecuteMultipleItems() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item1 = new CreateBillRequest.BillItemRequest();
        item1.setProductCode("P001");
        item1.setQuantity(2);

        CreateBillRequest.BillItemRequest item2 = new CreateBillRequest.BillItemRequest();
        item2.setProductCode("P002");
        item2.setQuantity(3);

        request.setItems(Arrays.asList(item1, item2));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("500"));

        Product product1 = new Product();
        product1.setProductCode("P001");
        product1.setUnitPrice(new BigDecimal("100"));
        product1.setShelfQuantity(10);
        product1.setDeleted(false);

        Product product2 = new Product();
        product2.setProductCode("P002");
        product2.setUnitPrice(new BigDecimal("50"));
        product2.setShelfQuantity(10);
        product2.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product1));
        when(productDao.findByProductCode("P002")).thenReturn(Optional.of(product2));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(productDao, times(1)).findByProductCode("P001");
        verify(productDao, times(1)).findByProductCode("P002");
    }

    @Test
    public void testExecuteCardPayment() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CARD");

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(billDao).createBill(anyString(), anyString(), anyString(), eq("CARD"), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteExactPayment() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("100"));

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(billDao).createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), eq(new BigDecimal("100")), eq(BigDecimal.ZERO), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteLargeQuantity() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(100);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("10000"));

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setShelfQuantity(150);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
    }

    @Test
    public void testExecuteOnlineWithDeliveryPhone() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setChannel("ONLINE");
        request.setPaymentMethod("CARD");
        request.setDeliveryAddress("123 Street");
        request.setDeliveryCity("City");
        request.setDeliveryPostalCode("12345");
        request.setDeliveryPhone("0771234567");

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setWebsiteQuantity(10);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), eq("0771234567"), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001", "CUSTOMER");

        assertNotNull(result);
    }

    @Test
    public void testExecuteHighDiscountScenario() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(1);
        request.setItems(Collections.singletonList(item));
        request.setPaymentMethod("CASH");
        request.setAmountPaid(new BigDecimal("100"));

        Product product = mock(Product.class);
        when(product.getProductCode()).thenReturn("P001");
        when(product.getName()).thenReturn("Product 1");
        when(product.getUnitPrice()).thenReturn(new BigDecimal("100"));
        when(product.getShelfQuantity()).thenReturn(10);
        when(product.isDeleted()).thenReturn(false);
        when(product.hasActiveDiscount()).thenReturn(true);
        when(product.getDiscountedPrice()).thenReturn(new BigDecimal("50"));
        when(product.getDiscountPercentage()).thenReturn(new BigDecimal("50"));

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));
        when(billDao.createBill(anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn("B001");
        when(billDao.getBillByNumber("B001")).thenReturn(new BillDTO());

        BillDTO result = createBillUseCase.execute(request, "U001");

        assertNotNull(result);
        verify(billDao).createBill(anyString(), anyString(), anyString(), anyString(), eq(new BigDecimal("50")), eq(new BigDecimal("50")), eq(new BigDecimal("50")), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testExecuteWebsiteStockCheck() throws SQLException {
        CreateBillRequest request = new CreateBillRequest();
        CreateBillRequest.BillItemRequest item = new CreateBillRequest.BillItemRequest();
        item.setProductCode("P001");
        item.setQuantity(5);
        request.setItems(Collections.singletonList(item));
        request.setChannel("ONLINE");
        request.setPaymentMethod("CARD");
        request.setDeliveryAddress("123 Street");
        request.setDeliveryCity("City");
        request.setDeliveryPostalCode("12345");

        Product product = new Product();
        product.setProductCode("P001");
        product.setUnitPrice(new BigDecimal("100"));
        product.setWebsiteQuantity(3);
        product.setDeleted(false);

        when(productDao.findByProductCode("P001")).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request, "U001", "CUSTOMER"));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }
}
