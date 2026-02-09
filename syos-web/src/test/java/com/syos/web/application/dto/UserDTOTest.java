package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class UserDTOTest {

    private UserDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new UserDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        UserDTO fullDto = new UserDTO("U001", "John Doe", "john@test.com", "0771234567", 1);
        assertEquals("U001", fullDto.getUserId());
        assertEquals("John Doe", fullDto.getFullName());
        assertEquals("john@test.com", fullDto.getEmail());
        assertEquals("0771234567", fullDto.getContactNumber());
        assertEquals(1, fullDto.getRoleId());
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
        dto.setEmail("user@test.com");
        assertEquals("user@test.com", dto.getEmail());
    }

    @Test
    public void testSetAndGetContactNumber() {
        dto.setContactNumber("0771234567");
        assertEquals("0771234567", dto.getContactNumber());
    }

    @Test
    public void testSetAndGetRoleId() {
        dto.setRoleId(1);
        assertEquals(1, dto.getRoleId());
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
    public void testEmptyContactNumber() {
        dto.setContactNumber("");
        assertEquals("", dto.getContactNumber());
    }

    @Test
    public void testWhitespaceValues() {
        dto.setFullName("   ");
        dto.setEmail("   ");
        dto.setUserId("   ");
        assertEquals("   ", dto.getFullName());
        assertEquals("   ", dto.getEmail());
        assertEquals("   ", dto.getUserId());
    }

    @Test
    public void testRoleIdCustomer() {
        dto.setRoleId(1);
        assertEquals(1, dto.getRoleId());
    }

    @Test
    public void testRoleIdCashier() {
        dto.setRoleId(2);
        assertEquals(2, dto.getRoleId());
    }

    @Test
    public void testRoleIdAdmin() {
        dto.setRoleId(3);
        assertEquals(3, dto.getRoleId());
    }

    @Test
    public void testZeroRoleId() {
        dto.setRoleId(0);
        assertEquals(0, dto.getRoleId());
    }

    @Test
    public void testNegativeRoleId() {
        dto.setRoleId(-1);
        assertEquals(-1, dto.getRoleId());
    }

    @Test
    public void testLongFullName() {
        String longName = "A".repeat(300);
        dto.setFullName(longName);
        assertEquals(300, dto.getFullName().length());
    }

    @Test
    public void testSpecialCharsInFullName() {
        dto.setFullName("O'Brien-Smith");
        assertEquals("O'Brien-Smith", dto.getFullName());
    }

    @Test
    public void testUnicodeInFullName() {
        dto.setFullName("José García");
        assertEquals("José García", dto.getFullName());
    }

    @Test
    public void testValidEmailFormat() {
        dto.setEmail("user@domain.com");
        assertTrue(dto.getEmail().contains("@"));
        assertTrue(dto.getEmail().contains("."));
    }

    @Test
    public void testInvalidEmailFormat() {
        dto.setEmail("invalidemail");
        assertFalse(dto.getEmail().contains("@"));
    }

    @Test
    public void testCompleteUserScenario() {
        dto.setUserId("U001");
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setContactNumber("0771234567");
        dto.setRoleId(1);

        assertNotNull(dto.getUserId());
        assertNotNull(dto.getFullName());
        assertNotNull(dto.getEmail());
        assertNotNull(dto.getContactNumber());
        assertEquals(1, dto.getRoleId());
    }

    @Test
    public void testContactNumberWithDashes() {
        dto.setContactNumber("077-123-4567");
        assertEquals("077-123-4567", dto.getContactNumber());
    }

    @Test
    public void testContactNumberWithSpaces() {
        dto.setContactNumber("077 123 4567");
        assertEquals("077 123 4567", dto.getContactNumber());
    }
}

