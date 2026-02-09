package com.syos.web.application.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CashierDTOTest {

    private CashierDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new CashierDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        CashierDTO fullDto = new CashierDTO("C001", "John Doe", "john@test.com", "0771234567", true);

        assertEquals("C001", fullDto.getUserId());
        assertEquals("John Doe", fullDto.getFullName());
        assertEquals("john@test.com", fullDto.getEmail());
        assertEquals("0771234567", fullDto.getContactNumber());
        assertTrue(fullDto.isActive());
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
    public void testSetAndGetActive() {
        dto.setActive(true);
        assertTrue(dto.isActive());
    }

    @Test
    public void testActiveFalse() {
        dto.setActive(false);
        assertFalse(dto.isActive());
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
    public void testEmptyFullName() {
        dto.setFullName("");
        assertEquals("", dto.getFullName());
    }

    @Test
    public void testWhitespaceFullName() {
        dto.setFullName("   ");
        assertEquals("   ", dto.getFullName());
    }

    @Test
    public void testLongFullName() {
        String longName = "A".repeat(200);
        dto.setFullName(longName);
        assertEquals(200, dto.getFullName().length());
    }

    @Test
    public void testSpecialCharsInFullName() {
        dto.setFullName("O'Brien-Smith");
        assertEquals("O'Brien-Smith", dto.getFullName());
    }

    @Test
    public void testEmptyContactNumber() {
        dto.setContactNumber("");
        assertEquals("", dto.getContactNumber());
    }

    @Test
    public void testFormattedContactNumber() {
        dto.setContactNumber("077-123-4567");
        assertEquals("077-123-4567", dto.getContactNumber());
    }

    @Test
    public void testContactNumberWithSpaces() {
        dto.setContactNumber("077 123 4567");
        assertEquals("077 123 4567", dto.getContactNumber());
    }

    @Test
    public void testCompleteCashierScenario() {
        dto.setUserId("C002");
        dto.setFullName("Jane Doe");
        dto.setEmail("jane@test.com");
        dto.setContactNumber("0771234567");
        dto.setActive(true);

        assertNotNull(dto.getUserId());
        assertNotNull(dto.getFullName());
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getContactNumber());
        assertTrue(dto.isActive());
    }

    @Test
    public void testInactiveCashier() {
        dto.setUserId("C003");
        dto.setFullName("John Smith");
        dto.setEmail("john.smith@test.com");
        dto.setContactNumber("0779876543");
        dto.setActive(false);

        assertFalse(dto.isActive());
    }

    @Test
    public void testEmailFormat() {
        dto.setEmail("user@domain.com");
        assertTrue(dto.getEmail().contains("@"));
        assertTrue(dto.getEmail().contains("."));
    }

    @Test
    public void testInvalidEmailFormat() {
        dto.setEmail("notanemail");
        assertFalse(dto.getEmail().contains("@"));
    }
}

