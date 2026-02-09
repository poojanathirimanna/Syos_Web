package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterRequestTest {

    private RegisterRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new RegisterRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        RegisterRequest fullDto = new RegisterRequest("U001", "John Doe", "john@test.com", "0771234567", "password123");
        assertEquals("U001", fullDto.getUserId());
        assertEquals("John Doe", fullDto.getFullName());
        assertEquals("john@test.com", fullDto.getEmail());
        assertEquals("0771234567", fullDto.getContactNumber());
        assertEquals("password123", fullDto.getPassword());
    }

    @Test
    public void testSetAndGetUserId() {
        dto.setUserId("U001");
        assertEquals("U001", dto.getUserId());
    }

    @Test
    public void testSetAndGetFullName() {
        dto.setFullName("John Doe");
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    public void testSetAndGetEmail() {
        dto.setEmail("test@example.com");
        assertEquals("test@example.com", dto.getEmail());
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
    public void testNullFullName() {
        dto.setFullName(null);
        assertNull(dto.getFullName());
    }

    @Test
    public void testNullEmail() {
        dto.setEmail(null);
        assertNull(dto.getEmail());
    }

    @Test
    public void testNullContactNumber() {
        dto.setContactNumber(null);
        assertNull(dto.getContactNumber());
    }

    @Test
    public void testNullPassword() {
        dto.setPassword(null);
        assertNull(dto.getPassword());
    }

    @Test
    public void testEmptyUserId() {
        dto.setUserId("");
        assertEquals("", dto.getUserId());
    }

    @Test
    public void testEmptyFullName() {
        dto.setFullName("");
        assertEquals("", dto.getFullName());
    }

    @Test
    public void testEmptyEmail() {
        dto.setEmail("");
        assertEquals("", dto.getEmail());
    }

    @Test
    public void testEmptyPassword() {
        dto.setPassword("");
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testWhitespaceValues() {
        dto.setEmail("   ");
        dto.setPassword("   ");
        dto.setFullName("   ");
        assertEquals("   ", dto.getEmail());
        assertEquals("   ", dto.getPassword());
        assertEquals("   ", dto.getFullName());
    }

    @Test
    public void testValidEmailFormat() {
        dto.setEmail("user@domain.com");
        assertTrue(dto.getEmail().contains("@"));
        assertTrue(dto.getEmail().contains("."));
    }

    @Test
    public void testInvalidEmailNoAt() {
        dto.setEmail("userdomaincom");
        assertFalse(dto.getEmail().contains("@"));
    }

    @Test
    public void testPasswordWithSpecialChars() {
        dto.setPassword("P@ssw0rd!");
        assertEquals("P@ssw0rd!", dto.getPassword());
    }

    @Test
    public void testWeakPassword() {
        dto.setPassword("123");
        assertEquals("123", dto.getPassword());
    }

    @Test
    public void testStrongPassword() {
        dto.setPassword("SecureP@ssw0rd2024!");
        assertEquals("SecureP@ssw0rd2024!", dto.getPassword());
    }

    @Test
    public void testLongFullName() {
        String longName = "A".repeat(200);
        dto.setFullName(longName);
        assertEquals(200, dto.getFullName().length());
    }

    @Test
    public void testFullNameWithSpecialChars() {
        dto.setFullName("O'Brien-Smith Jr.");
        assertEquals("O'Brien-Smith Jr.", dto.getFullName());
    }

    @Test
    public void testContactNumberFormats() {
        dto.setContactNumber("077-123-4567");
        assertEquals("077-123-4567", dto.getContactNumber());
    }

    @Test
    public void testCompleteValidRegistration() {
        dto.setUserId("U002");
        dto.setFullName("Jane Doe");
        dto.setEmail("newuser@example.com");
        dto.setContactNumber("0771234567");
        dto.setPassword("SecureP@ss123");

        assertNotNull(dto.getUserId());
        assertNotNull(dto.getFullName());
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getContactNumber());
        assertNotNull(dto.getPassword());
    }
}

