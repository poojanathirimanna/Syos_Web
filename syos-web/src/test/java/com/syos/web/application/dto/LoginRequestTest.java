package com.syos.web.application.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    private LoginRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new LoginRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        LoginRequest fullDto = new LoginRequest("user@test.com", "password123");
        assertEquals("user@test.com", fullDto.getUsername());
        assertEquals("password123", fullDto.getPassword());
    }

    @Test
    public void testSetAndGetUsername() {
        dto.setUsername("user@test.com");
        assertEquals("user@test.com", dto.getUsername());
    }

    @Test
    public void testSetAndGetPassword() {
        dto.setPassword("password123");
        assertEquals("password123", dto.getPassword());
    }

    @Test
    public void testNullUsername() {
        dto.setUsername(null);
        assertNull(dto.getUsername());
    }

    @Test
    public void testNullPassword() {
        dto.setPassword(null);
        assertNull(dto.getPassword());
    }

    @Test
    public void testEmptyUsername() {
        dto.setUsername("");
        assertEquals("", dto.getUsername());
    }

    @Test
    public void testEmptyPassword() {
        dto.setPassword("");
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testWhitespaceUsername() {
        dto.setUsername("   ");
        assertEquals("   ", dto.getUsername());
    }

    @Test
    public void testWhitespacePassword() {
        dto.setPassword("   ");
        assertEquals("   ", dto.getPassword());
    }

    @Test
    public void testEmailFormatUsername() {
        dto.setUsername("user@example.com");
        assertTrue(dto.getUsername().contains("@"));
        assertTrue(dto.getUsername().contains("."));
    }

    @Test
    public void testNonEmailUsername() {
        dto.setUsername("notanemail");
        assertFalse(dto.getUsername().contains("@"));
    }

    @Test
    public void testLongPassword() {
        String longPassword = "A".repeat(100);
        dto.setPassword(longPassword);
        assertEquals(100, dto.getPassword().length());
    }

    @Test
    public void testSpecialCharsInPassword() {
        dto.setPassword("P@ssw0rd!#$%");
        assertEquals("P@ssw0rd!#$%", dto.getPassword());
    }

    @Test
    public void testCompleteLoginScenario() {
        dto.setUsername("user@example.com");
        dto.setPassword("securePassword123");

        assertNotNull(dto.getUsername());
        assertNotNull(dto.getPassword());
        assertTrue(dto.getUsername().contains("@"));
        assertTrue(dto.getPassword().length() > 8);
    }

    @Test
    public void testEmptyCredentials() {
        dto.setUsername("");
        dto.setPassword("");

        assertEquals("", dto.getUsername());
        assertEquals("", dto.getPassword());
    }

    @Test
    public void testShortPassword() {
        dto.setPassword("123");
        assertEquals("123", dto.getPassword());
    }

    @Test
    public void testPasswordWithSpaces() {
        dto.setPassword("pass word");
        assertEquals("pass word", dto.getPassword());
    }

    @Test
    public void testUnicodePassword() {
        dto.setPassword("пароль123");
        assertEquals("пароль123", dto.getPassword());
    }

    @Test
    public void testCaseSensitivePassword() {
        dto.setPassword("Password");
        assertNotEquals("password", dto.getPassword());
    }
}

