package com.syos.web.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    public void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(cart);
    }

    @Test
    public void testConstructorWithoutId() {
        ShoppingCart c = new ShoppingCart("U001", "P001", 5);
        assertEquals("U001", c.getUserId());
        assertEquals("P001", c.getProductCode());
        assertEquals(5, c.getQuantity());
    }

    @Test
    public void testFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ShoppingCart c = new ShoppingCart(1, "U001", "P001", 5, now, now);
        assertEquals(1, c.getCartId());
        assertEquals("U001", c.getUserId());
        assertEquals("P001", c.getProductCode());
        assertEquals(5, c.getQuantity());
        assertEquals(now, c.getAddedAt());
        assertEquals(now, c.getUpdatedAt());
    }

    @Test
    public void testSetAndGetCartId() {
        cart.setCartId(1);
        assertEquals(1, cart.getCartId());
    }

    @Test
    public void testSetAndGetUserId() {
        cart.setUserId("U001");
        assertEquals("U001", cart.getUserId());
    }

    @Test
    public void testSetAndGetProductCode() {
        cart.setProductCode("P001");
        assertEquals("P001", cart.getProductCode());
    }

    @Test
    public void testSetAndGetQuantity() {
        cart.setQuantity(10);
        assertEquals(10, cart.getQuantity());
    }

    @Test
    public void testSetAndGetAddedAt() {
        LocalDateTime now = LocalDateTime.now();
        cart.setAddedAt(now);
        assertEquals(now, cart.getAddedAt());
    }

    @Test
    public void testSetAndGetUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        cart.setUpdatedAt(now);
        assertEquals(now, cart.getUpdatedAt());
    }

    @Test
    public void testValidateSuccess() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testValidateNullUserId() {
        cart.setUserId(null);
        cart.setProductCode("P001");
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateEmptyUserId() {
        cart.setUserId("");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateWhitespaceUserId() {
        cart.setUserId("   ");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        cart.setUserId("U001");
        cart.setProductCode(null);
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        cart.setUserId("U001");
        cart.setProductCode("");
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        cart.setUserId("U001");
        cart.setProductCode("   ");
        cart.setQuantity(5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateZeroQuantity() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testValidateNegativeQuantity() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(-5);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }

    @Test
    public void testNullValues() {
        cart.setCartId(null);
        cart.setUserId(null);
        cart.setProductCode(null);
        cart.setAddedAt(null);
        cart.setUpdatedAt(null);

        assertNull(cart.getCartId());
        assertNull(cart.getUserId());
        assertNull(cart.getProductCode());
        assertNull(cart.getAddedAt());
        assertNull(cart.getUpdatedAt());
    }

    @Test
    public void testQuantityBoundaries() {
        cart.setQuantity(1);
        assertEquals(1, cart.getQuantity());

        cart.setQuantity(1000);
        assertEquals(1000, cart.getQuantity());

        cart.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, cart.getQuantity());
    }

    @Test
    public void testTimestampOperations() {
        LocalDateTime added = LocalDateTime.now().minusHours(1);
        LocalDateTime updated = LocalDateTime.now();

        cart.setAddedAt(added);
        cart.setUpdatedAt(updated);

        assertTrue(cart.getAddedAt().isBefore(cart.getUpdatedAt()));
    }

    @Test
    public void testCompleteScenario() {
        cart.setCartId(1);
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        cart.setAddedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        assertNotNull(cart.getCartId());
        assertNotNull(cart.getUserId());
        assertNotNull(cart.getProductCode());
        assertTrue(cart.getQuantity() > 0);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testUserIdFormats() {
        String[] userIds = {"U001", "CUSTOMER123", "user@test.com", "12345"};
        for (String userId : userIds) {
            cart.setUserId(userId);
            cart.setProductCode("P001");
            cart.setQuantity(1);
            assertDoesNotThrow(() -> cart.validate());
        }
    }

    @Test
    public void testProductCodeFormats() {
        String[] productCodes = {"P001", "PROD-123", "SKU_456", "123"};
        for (String code : productCodes) {
            cart.setUserId("U001");
            cart.setProductCode(code);
            cart.setQuantity(1);
            assertDoesNotThrow(() -> cart.validate());
        }
    }

    @Test
    public void testMultipleValidations() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(5);

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> cart.validate());
        }
    }

    @Test
    public void testUpdateQuantity() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        assertDoesNotThrow(() -> cart.validate());

        cart.setQuantity(10);
        assertDoesNotThrow(() -> cart.validate());
        assertEquals(10, cart.getQuantity());
    }

    @Test
    public void testLargeQuantity() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(99999);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testMinimalValidCart() {
        cart.setUserId("U");
        cart.setProductCode("P");
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testLongUserId() {
        String longUserId = "U" + "0".repeat(100);
        cart.setUserId(longUserId);
        cart.setProductCode("P001");
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        cart.setUserId("U001");
        cart.setProductCode(longCode);
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testSpecialCharsInUserId() {
        cart.setUserId("user@test.com");
        cart.setProductCode("P001");
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testSpecialCharsInProductCode() {
        cart.setUserId("U001");
        cart.setProductCode("P-001_A");
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
    }

    @Test
    public void testTimestampNull() {
        cart.setAddedAt(null);
        cart.setUpdatedAt(null);
        assertNull(cart.getAddedAt());
        assertNull(cart.getUpdatedAt());
    }

    @Test
    public void testCartIdNull() {
        cart.setCartId(null);
        assertNull(cart.getCartId());
    }

    @Test
    public void testCartIdLarge() {
        cart.setCartId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, cart.getCartId());
    }

    @Test
    public void testCartIdZero() {
        cart.setCartId(0);
        assertEquals(0, cart.getCartId());
    }

    @Test
    public void testCartIdNegative() {
        cart.setCartId(-1);
        assertEquals(-1, cart.getCartId());
    }

    @Test
    public void testQuantityOne() {
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(1);
        assertDoesNotThrow(() -> cart.validate());
        assertEquals(1, cart.getQuantity());
    }

    @Test
    public void testAllFieldsSet() {
        LocalDateTime now = LocalDateTime.now();
        cart.setCartId(1);
        cart.setUserId("U001");
        cart.setProductCode("P001");
        cart.setQuantity(5);
        cart.setAddedAt(now);
        cart.setUpdatedAt(now);

        assertEquals(1, cart.getCartId());
        assertEquals("U001", cart.getUserId());
        assertEquals("P001", cart.getProductCode());
        assertEquals(5, cart.getQuantity());
        assertEquals(now, cart.getAddedAt());
        assertEquals(now, cart.getUpdatedAt());
    }

    @Test
    public void testUpdateTimestamp() {
        LocalDateTime added = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        cart.setAddedAt(added);
        cart.setUpdatedAt(updated);

        assertNotNull(cart.getAddedAt());
        assertNotNull(cart.getUpdatedAt());
        assertTrue(cart.getUpdatedAt().isAfter(cart.getAddedAt()));
    }

    @Test
    public void testPastTimestamps() {
        LocalDateTime past = LocalDateTime.now().minusYears(1);
        cart.setAddedAt(past);
        cart.setUpdatedAt(past);

        assertEquals(past, cart.getAddedAt());
        assertEquals(past, cart.getUpdatedAt());
    }

    @Test
    public void testFutureTimestamps() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        cart.setAddedAt(future);
        cart.setUpdatedAt(future);

        assertEquals(future, cart.getAddedAt());
        assertEquals(future, cart.getUpdatedAt());
    }

    @Test
    public void testValidateAllFieldsNull() {
        cart.setUserId(null);
        cart.setProductCode(null);
        cart.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> cart.validate());
    }
}

