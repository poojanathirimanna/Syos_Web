package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CreateCashierRequestTest {

    private CreateCashierRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new CreateCashierRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testCompleteSetup() {
        CreateCashierRequest request = new CreateCashierRequest();
        request.setUserId("C001");
        request.setFullName("John Cashier");
        request.setEmail("john@test.com");
        request.setContactNumber("0771234567");
        request.setPassword("password123");

        assertEquals("C001", request.getUserId());
        assertEquals("John Cashier", request.getFullName());
        assertEquals("john@test.com", request.getEmail());
        assertEquals("0771234567", request.getContactNumber());
        assertEquals("password123", request.getPassword());
    }

    @Test
    public void testSetAndGetUserId() {
        dto.setUserId("C001");
        assertEquals("C001", dto.getUserId());
    }

    @Test
    public void testSetAndGetFullName() {
        dto.setFullName("John Doe");
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    public void testSetAndGetEmail() {
        dto.setEmail("john@test.com");
        assertEquals("john@test.com", dto.getEmail());
    }

    @Test
    public void testSetAndGetContactNumber() {
        dto.setContactNumber("0771234567");
        assertEquals("0771234567", dto.getContactNumber());
    }

    @Test
    public void testSetAndGetPassword() {
        dto.setPassword("password123");
        assertEquals("password123", dto.getPassword());
    }

    @Test
    public void testNullUserId() {
        dto.setUserId(null);
        assertNull(dto.getUserId());
    }

    @Test
    public void testEmptyUserId() {
        dto.setUserId("");
        assertEquals("", dto.getUserId());
    }

    @Test
    public void testWhitespaceUserId() {
        dto.setUserId("   ");
        assertEquals("   ", dto.getUserId());
    }

    @Test
    public void testNullFullName() {
        dto.setFullName(null);
        assertNull(dto.getFullName());
    }

    @Test
    public void testEmptyFullName() {
        dto.setFullName("");
        assertEquals("", dto.getFullName());
    }

    @Test
    public void testNullEmail() {
        dto.setEmail(null);
        assertNull(dto.getEmail());
    }

    @Test
    public void testEmptyEmail() {
        dto.setEmail("");
        assertEquals("", dto.getEmail());
    }

    @Test
    public void testNullContactNumber() {
        dto.setContactNumber(null);
        assertNull(dto.getContactNumber());
    }

    @Test
    public void testEmptyContactNumber() {
        dto.setContactNumber("");
        assertEquals("", dto.getContactNumber());
    }

    @Test
    public void testNullPassword() {
        dto.setPassword(null);
        assertNull(dto.getPassword());
    }

    @Test
    public void testEmptyPassword() {
        dto.setPassword("");
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testLongFullName() {
        String longName = "A".repeat(500);
        dto.setFullName(longName);
        assertEquals(500, dto.getFullName().length());
    }

    @Test
    public void testSpecialCharsInName() {
        dto.setFullName("O'Brien-Smith Jr.");
        assertEquals("O'Brien-Smith Jr.", dto.getFullName());
    }

    @Test
    public void testUnicodeInName() {
        dto.setFullName("José García");
        assertEquals("José García", dto.getFullName());
    }

    @Test
    public void testEmailFormats() {
        String[] emails = {"test@test.com", "user+tag@domain.co.uk", "user.name@test.com"};
        for (String email : emails) {
            dto.setEmail(email);
            assertEquals(email, dto.getEmail());
        }
    }

    @Test
    public void testContactNumberFormats() {
        String[] numbers = {"0771234567", "077-123-4567", "077 123 4567", "+94771234567"};
        for (String number : numbers) {
            dto.setContactNumber(number);
            assertEquals(number, dto.getContactNumber());
        }
    }

    @Test
    public void testShortPassword() {
        dto.setPassword("123");
        assertEquals("123", dto.getPassword());
    }

    @Test
    public void testLongPassword() {
        String longPassword = "A".repeat(200);
        dto.setPassword(longPassword);
        assertEquals(200, dto.getPassword().length());
    }

    @Test
    public void testPasswordWithSpecialChars() {
        dto.setPassword("P@ssw0rd!#$%");
        assertEquals("P@ssw0rd!#$%", dto.getPassword());
    }

    @Test
    public void testPasswordWithSpaces() {
        dto.setPassword("pass word");
        assertEquals("pass word", dto.getPassword());
    }

    @Test
    public void testCompleteScenario() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setContactNumber("0771234567");
        dto.setPassword("SecureP@ss123");

        assertNotNull(dto.getUserId());
        assertNotNull(dto.getFullName());
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getContactNumber());
        assertNotNull(dto.getPassword());
    }

    @Test
    public void testAllNullFields() {
        dto.setUserId(null);
        dto.setFullName(null);
        dto.setEmail(null);
        dto.setContactNumber(null);
        dto.setPassword(null);

        assertNull(dto.getUserId());
        assertNull(dto.getFullName());
        assertNull(dto.getEmail());
        assertNull(dto.getContactNumber());
        assertNull(dto.getPassword());
    }

    @Test
    public void testAllEmptyFields() {
        dto.setUserId("");
        dto.setFullName("");
        dto.setEmail("");
        dto.setContactNumber("");
        dto.setPassword("");

        assertEquals("", dto.getUserId());
        assertEquals("", dto.getFullName());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getContactNumber());
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testUserIdFormats() {
        String[] userIds = {"C001", "CASHIER123", "12345"};
        for (String userId : userIds) {
            dto.setUserId(userId);
            assertEquals(userId, dto.getUserId());
        }
    }

    @Test
    public void testMinimalName() {
        dto.setFullName("A");
        assertEquals("A", dto.getFullName());
    }

    @Test
    public void testMinimalEmail() {
        dto.setEmail("a@b.c");
        assertEquals("a@b.c", dto.getEmail());
    }

    @Test
    public void testNumericContactNumber() {
        dto.setContactNumber("1234567890");
        assertEquals("1234567890", dto.getContactNumber());
    }

    @Test
    public void testAlphaNumericUserId() {
        dto.setUserId("C123ABC");
        assertEquals("C123ABC", dto.getUserId());
    }

    @Test
    public void testEmailWithDots() {
        dto.setEmail("user.name.test@domain.com");
        assertEquals("user.name.test@domain.com", dto.getEmail());
    }

    @Test
    public void testPasswordNumericOnly() {
        dto.setPassword("12345678");
        assertEquals("12345678", dto.getPassword());
    }

    @Test
    public void testPasswordAlphaOnly() {
        dto.setPassword("abcdefgh");
        assertEquals("abcdefgh", dto.getPassword());
    }

    @Test
    public void testPasswordMixed() {
        dto.setPassword("Pass123!@#");
        assertEquals("Pass123!@#", dto.getPassword());
    }

    @Test
    public void testValidateSuccess() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullUserId() {
        dto.setUserId(null);
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyUserId() {
        dto.setUserId("");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateShortUserId() {
        dto.setUserId("AB");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullFullName() {
        dto.setUserId("C001");
        dto.setFullName(null);
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyFullName() {
        dto.setUserId("C001");
        dto.setFullName("");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullEmail() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail(null);
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyEmail() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateInvalidEmailFormat() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("notanemail");
        dto.setPassword("password123");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullPassword() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateShortPassword() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("12345");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateMinimumPasswordLength() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("123456");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMinimumUserIdLength() {
        dto.setUserId("ABC");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setPassword("password123");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWithContactNumber() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setContactNumber("0771234567");
        dto.setPassword("password123");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWithNullContactNumber() {
        dto.setUserId("C001");
        dto.setFullName("John Cashier");
        dto.setEmail("john@test.com");
        dto.setContactNumber(null);
        dto.setPassword("password123");
        assertDoesNotThrow(() -> dto.validate());
    }
}

