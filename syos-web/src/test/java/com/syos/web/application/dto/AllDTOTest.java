package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllDTOTest {

    @Test
    public void testAllDTOs() throws Exception {
        Class<?>[] dtoClasses = {
            AddressDTO.class,
            AddReviewRequest.class,
            AddToCartRequest.class,
            ApiResponse.class,
            AvailableProductDTO.class,
            BillDTO.class,
            BillItemDTO.class,
            CartItemDTO.class,
            CartResponseDTO.class,
            CartSummaryDTO.class,
            CashierDTO.class,
            CreateBillRequest.class,
            CreateCashierRequest.class,
            CreateProductRequest.class,
            InventoryLocationDTO.class,
            InventorySummaryDTO.class,
            LoginRequest.class,
            ProductCatalogDTO.class,
            ProductDTO.class,
            PromotionDTO.class,
            ReceiveStockRequest.class,
            RegisterRequest.class,
            ReviewDTO.class,
            SetBatchDiscountRequest.class,
            SetProductDiscountRequest.class,
            StockBatchDTO.class,
            TransferStockRequest.class,
            TransferStockResponse.class,
            UpdateCartRequest.class,
            UpdateCashierRequest.class,
            UpdateProductRequest.class,
            UserDTO.class,
            WishlistItemDTO.class
        };

        for (Class<?> clazz : dtoClasses) {
            testDTO(clazz);
        }
    }

    private void testDTO(Class<?> clazz) throws Exception {
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            // If no default constructor, skip or handle specifically
            return;
        }
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (isGetter(method)) {
                String setterName = "set" + method.getName().substring(method.getName().startsWith("is") ? 2 : 3);
                try {
                    Method setter = clazz.getMethod(setterName, method.getReturnType());
                    Object value = getDefaultValue(method.getReturnType());
                    setter.invoke(instance, value);
                    assertEquals(value, method.invoke(instance), "Failed for " + clazz.getSimpleName() + "." + method.getName());
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
    }

    private boolean isGetter(Method method) {
        if (method.getParameterCount() != 0) return false;
        if (method.getName().startsWith("get") && !method.getName().equals("getClass")) return true;
        if (method.getName().startsWith("is") && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) return true;
        return false;
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == String.class) return "test";
        if (type == int.class || type == Integer.class) return 1;
        if (type == long.class || type == Long.class) return 1L;
        if (type == boolean.class || type == Boolean.class) return true;
        if (type == BigDecimal.class) return BigDecimal.ONE;
        if (type == LocalDate.class) return LocalDate.now();
        if (type == LocalDateTime.class) return LocalDateTime.now();
        if (type == List.class) return new ArrayList<>();
        return null;
    }
}
