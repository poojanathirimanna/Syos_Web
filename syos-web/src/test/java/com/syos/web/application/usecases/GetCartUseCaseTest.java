package com.syos.web.application.usecases;

import com.syos.web.application.dto.CartItemDTO;
import com.syos.web.application.dto.CartResponseDTO;
import com.syos.web.application.dto.CartSummaryDTO;
import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetCartUseCaseTest {

    @Mock
    private ShoppingCartDao cartDao;

    private GetCartUseCase getCartUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        getCartUseCase = new GetCartUseCase();
    }

    @Test
    public void testExecuteWithEmptyCart() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> emptyList = new ArrayList<>();

        when(cartDao.getCartItems(userId)).thenReturn(emptyList);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        assertEquals(0, response.getSummary().getItemCount());
        assertEquals(BigDecimal.ZERO, response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteWithSingleItem() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setProductName("Product 1");
        item.setQuantity(2);
        item.setOriginalPrice(new BigDecimal("100.00"));
        item.setDiscountPercentage(BigDecimal.ZERO);
        item.setSubtotal(new BigDecimal("200.00"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getSummary().getItemCount());
        assertEquals(new BigDecimal("200.00"), response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteWithMultipleItems() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item1 = new CartItemDTO();
        item1.setProductCode("P001");
        item1.setQuantity(2);
        item1.setOriginalPrice(new BigDecimal("100.00"));
        item1.setDiscountPercentage(BigDecimal.ZERO);
        item1.setSubtotal(new BigDecimal("200.00"));
        items.add(item1);

        CartItemDTO item2 = new CartItemDTO();
        item2.setProductCode("P002");
        item2.setQuantity(1);
        item2.setOriginalPrice(new BigDecimal("50.00"));
        item2.setDiscountPercentage(BigDecimal.ZERO);
        item2.setSubtotal(new BigDecimal("50.00"));
        items.add(item2);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(2, response.getItems().size());
        assertEquals(2, response.getSummary().getItemCount());
        assertEquals(new BigDecimal("250.00"), response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteWithDiscount() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(1);
        item.setOriginalPrice(new BigDecimal("100.00"));
        item.setDiscountPercentage(new BigDecimal("10.00"));
        item.setSubtotal(new BigDecimal("90.00"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertNotNull(response);
        assertEquals(new BigDecimal("90.00"), response.getSummary().getSubtotal());
        assertEquals(new BigDecimal("10.00"), response.getSummary().getTotalDiscount());
    }

    @Test
    public void testExecuteSQLException() throws SQLException {
        String userId = "U001";

        when(cartDao.getCartItems(userId)).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> getCartUseCase.execute(userId));
    }

    @Test
    public void testExecuteNullUserId() throws SQLException {
        assertThrows(Exception.class, () -> getCartUseCase.execute(null));
    }

    @Test
    public void testExecuteEmptyUserId() throws SQLException {
        assertThrows(Exception.class, () -> getCartUseCase.execute(""));
    }

    @Test
    public void testDefaultConstructor() {
        GetCartUseCase useCase = new GetCartUseCase();
        assertNotNull(useCase);
    }

    @Test
    public void testExecuteWithLargeQuantity() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(100);
        item.setOriginalPrice(new BigDecimal("10.00"));
        item.setDiscountPercentage(BigDecimal.ZERO);
        item.setSubtotal(new BigDecimal("1000.00"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(new BigDecimal("1000.00"), response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteWithMultipleDiscounts() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item1 = new CartItemDTO();
        item1.setProductCode("P001");
        item1.setQuantity(1);
        item1.setOriginalPrice(new BigDecimal("100.00"));
        item1.setDiscountPercentage(new BigDecimal("20.00"));
        item1.setSubtotal(new BigDecimal("80.00"));
        items.add(item1);

        CartItemDTO item2 = new CartItemDTO();
        item2.setProductCode("P002");
        item2.setQuantity(1);
        item2.setOriginalPrice(new BigDecimal("50.00"));
        item2.setDiscountPercentage(new BigDecimal("10.00"));
        item2.setSubtotal(new BigDecimal("45.00"));
        items.add(item2);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(new BigDecimal("125.00"), response.getSummary().getSubtotal());
        assertEquals(new BigDecimal("25.00"), response.getSummary().getTotalDiscount());
    }

    @Test
    public void testExecuteWithZeroPriceItem() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(1);
        item.setOriginalPrice(BigDecimal.ZERO);
        item.setDiscountPercentage(BigDecimal.ZERO);
        item.setSubtotal(BigDecimal.ZERO);
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(BigDecimal.ZERO, response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteVerifyDaoInteraction() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        when(cartDao.getCartItems(userId)).thenReturn(items);

        getCartUseCase.execute(userId);

        verify(cartDao, times(1)).getCartItems(userId);
    }

    @Test
    public void testExecuteResponseStructure() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(1);
        item.setOriginalPrice(new BigDecimal("100.00"));
        item.setDiscountPercentage(BigDecimal.ZERO);
        item.setSubtotal(new BigDecimal("100.00"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertNotNull(response.getSummary());
        assertFalse(response.getItems().isEmpty());
    }

    @Test
    public void testExecuteSummaryCalculation() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            CartItemDTO item = new CartItemDTO();
            item.setProductCode("P00" + i);
            item.setQuantity(i);
            item.setOriginalPrice(new BigDecimal("10.00"));
            item.setDiscountPercentage(BigDecimal.ZERO);
            item.setSubtotal(new BigDecimal("10.00").multiply(new BigDecimal(i)));
            items.add(item);
        }

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(5, response.getSummary().getItemCount());
        // 10 + 20 + 30 + 40 + 50 = 150
        assertEquals(new BigDecimal("150.00"), response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteWithHighDiscount() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(1);
        item.setOriginalPrice(new BigDecimal("100.00"));
        item.setDiscountPercentage(new BigDecimal("90.00"));
        item.setSubtotal(new BigDecimal("10.00"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(new BigDecimal("10.00"), response.getSummary().getSubtotal());
        assertEquals(new BigDecimal("90.00"), response.getSummary().getTotalDiscount());
    }

    @Test
    public void testExecuteWithDecimalQuantities() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item = new CartItemDTO();
        item.setProductCode("P001");
        item.setQuantity(3);
        item.setOriginalPrice(new BigDecimal("33.33"));
        item.setDiscountPercentage(BigDecimal.ZERO);
        item.setSubtotal(new BigDecimal("99.99"));
        items.add(item);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(new BigDecimal("99.99"), response.getSummary().getSubtotal());
    }

    @Test
    public void testExecuteMixedDiscountAndNoDiscount() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        CartItemDTO item1 = new CartItemDTO();
        item1.setProductCode("P001");
        item1.setQuantity(1);
        item1.setOriginalPrice(new BigDecimal("100.00"));
        item1.setDiscountPercentage(BigDecimal.ZERO);
        item1.setSubtotal(new BigDecimal("100.00"));
        items.add(item1);

        CartItemDTO item2 = new CartItemDTO();
        item2.setProductCode("P002");
        item2.setQuantity(1);
        item2.setOriginalPrice(new BigDecimal("100.00"));
        item2.setDiscountPercentage(new BigDecimal("50.00"));
        item2.setSubtotal(new BigDecimal("50.00"));
        items.add(item2);

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(new BigDecimal("150.00"), response.getSummary().getSubtotal());
        assertEquals(new BigDecimal("50.00"), response.getSummary().getTotalDiscount());
    }

    @Test
    public void testExecuteDifferentUserIds() throws SQLException {
        String[] userIds = {"U001", "U002", "CUSTOMER123"};

        for (String userId : userIds) {
            List<CartItemDTO> items = new ArrayList<>();
            when(cartDao.getCartItems(userId)).thenReturn(items);

            CartResponseDTO response = getCartUseCase.execute(userId);
            assertNotNull(response);

            verify(cartDao, times(1)).getCartItems(userId);
            reset(cartDao);
        }
    }

    @Test
    public void testExecuteCartWithManyItems() throws SQLException {
        String userId = "U001";
        List<CartItemDTO> items = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            CartItemDTO item = new CartItemDTO();
            item.setProductCode("P" + String.format("%03d", i));
            item.setQuantity(1);
            item.setOriginalPrice(new BigDecimal("10.00"));
            item.setDiscountPercentage(BigDecimal.ZERO);
            item.setSubtotal(new BigDecimal("10.00"));
            items.add(item);
        }

        when(cartDao.getCartItems(userId)).thenReturn(items);

        CartResponseDTO response = getCartUseCase.execute(userId);

        assertEquals(50, response.getSummary().getItemCount());
        assertEquals(new BigDecimal("500.00"), response.getSummary().getSubtotal());
    }
}

