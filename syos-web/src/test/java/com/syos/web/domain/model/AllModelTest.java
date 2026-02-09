package com.syos.web.domain.model;

import com.syos.web.domain.enums.ProductStatus;
import com.syos.web.domain.enums.Role;
import com.syos.web.domain.enums.StockLocation;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllModelTest {

    @Test
    public void testAllModels() throws Exception {
        Class<?>[] modelClasses = {
            Bill.class,
            CustomerAddress.class,
            Product.class,
            ProductReview.class,
            ShoppingCart.class,
            StockBatch.class,
            Wishlist.class
        };

        for (Class<?> clazz : modelClasses) {
            testModel(clazz);
        }
    }

    @Test
    public void testEnums() {
        assertNotNull(ProductStatus.valueOf(ProductStatus.IN_STOCK.name()));
        assertNotNull(Role.valueOf(Role.MANAGER.name()));
        assertNotNull(StockLocation.valueOf(StockLocation.SHELF.name()));
        
        for (ProductStatus status : ProductStatus.values()) {
            assertNotNull(status.getDisplayName());
            assertNotNull(ProductStatus.fromQuantity(50, 20));
        }
        for (Role role : Role.values()) {
            assertNotNull(role.getRoleName());
            assertTrue(role.getRoleId() > 0);
            assertNotNull(Role.fromId(role.getRoleId()));
            assertNotNull(Role.fromName(role.getRoleName()));
        }
        for (StockLocation loc : StockLocation.values()) {
            assertNotNull(loc.getDisplayName());
            assertNotNull(loc.getTableName());
            assertNotNull(StockLocation.fromTableName(loc.getTableName()));
        }
    }

    private void testModel(Class<?> clazz) throws Exception {
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return;
        }
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (isGetter(method)) {
                String name = method.getName();
                String propertyName = name.startsWith("is") ? name.substring(2) : name.substring(3);
                String setterName = "set" + propertyName;
                try {
                    Method setter = clazz.getMethod(setterName, method.getReturnType());
                    Object value = getDefaultValue(method.getReturnType());
                    setter.invoke(instance, value);
                    assertEquals(value, method.invoke(instance), "Failed for " + clazz.getSimpleName() + "." + method.getName());
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
        
        // Test toString if it exists
        try {
            Method toString = clazz.getDeclaredMethod("toString");
            assertNotNull(instance.toString());
        } catch (NoSuchMethodException ignored) {}
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
        if (type == ProductStatus.class) return ProductStatus.IN_STOCK;
        if (type == Role.class) return Role.CUSTOMER;
        if (type == StockLocation.class) return StockLocation.SHELF;
        return null;
    }
}
