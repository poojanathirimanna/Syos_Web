package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddressDTOTest {

    @Test
    public void testGettersAndSetters() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(1);
        dto.setAddressLabel("Home");
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setAddressLine2("Apt 4");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        dto.setDefault(true);

        assertEquals(1, dto.getAddressId());
        assertEquals("Home", dto.getAddressLabel());
        assertEquals("John Doe", dto.getFullName());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("123 Main St", dto.getAddressLine1());
        assertEquals("Apt 4", dto.getAddressLine2());
        assertEquals("New York", dto.getCity());
        assertEquals("10001", dto.getPostalCode());
        assertTrue(dto.isDefault());
    }

    @Test
    public void testValidateSuccess() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        
        assertDoesNotThrow(dto::validate);
    }

    @Test
    public void testValidateFailureFullName() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        
        Exception exception = assertThrows(IllegalArgumentException.class, dto::validate);
        assertEquals("Full name is required", exception.getMessage());
    }

    @Test
    public void testValidateFailurePhone() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        
        Exception exception = assertThrows(IllegalArgumentException.class, dto::validate);
        assertEquals("Phone is required", exception.getMessage());
    }

    @Test
    public void testValidateFailureAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        
        Exception exception = assertThrows(IllegalArgumentException.class, dto::validate);
        assertEquals("Address is required", exception.getMessage());
    }

    @Test
    public void testValidateFailureCity() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setPostalCode("10001");
        
        Exception exception = assertThrows(IllegalArgumentException.class, dto::validate);
        assertEquals("City is required", exception.getMessage());
    }

    @Test
    public void testValidateFailurePostalCode() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        
        Exception exception = assertThrows(IllegalArgumentException.class, dto::validate);
        assertEquals("Postal code is required", exception.getMessage());
    }

    @Test
    public void testDefaultConstructor() {
        AddressDTO dto = new AddressDTO();
        assertNotNull(dto);
    }

    @Test
    public void testNullValues() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(null);
        dto.setAddressLabel(null);
        dto.setFullName(null);
        dto.setPhone(null);

        assertNull(dto.getAddressId());
        assertNull(dto.getAddressLabel());
        assertNull(dto.getFullName());
        assertNull(dto.getPhone());
    }

    @Test
    public void testEmptyStringValues() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("");
        dto.setPhone("");
        dto.setAddressLine1("");
        dto.setCity("");
        dto.setPostalCode("");

        assertEquals("", dto.getFullName());
        assertEquals("", dto.getPhone());
        assertEquals("", dto.getAddressLine1());
        assertEquals("", dto.getCity());
        assertEquals("", dto.getPostalCode());
    }

    @Test
    public void testWhitespaceValues() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("   ");
        dto.setPhone("   ");

        assertEquals("   ", dto.getFullName());
        assertEquals("   ", dto.getPhone());
    }

    @Test
    public void testIsDefaultFalse() {
        AddressDTO dto = new AddressDTO();
        dto.setDefault(false);
        assertFalse(dto.isDefault());
    }

    @Test
    public void testLongFullName() {
        AddressDTO dto = new AddressDTO();
        String longName = "A".repeat(500);
        dto.setFullName(longName);
        assertEquals(500, dto.getFullName().length());
    }

    @Test
    public void testSpecialCharsInName() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("O'Brien-Smith Jr.");
        assertEquals("O'Brien-Smith Jr.", dto.getFullName());
    }

    @Test
    public void testUnicodeInName() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("José García");
        assertEquals("José García", dto.getFullName());
    }

    @Test
    public void testPhoneWithDashes() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("077-123-4567");
        assertEquals("077-123-4567", dto.getPhone());
    }

    @Test
    public void testPhoneWithSpaces() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("077 123 4567");
        assertEquals("077 123 4567", dto.getPhone());
    }

    @Test
    public void testInternationalPhone() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("+94771234567");
        assertEquals("+94771234567", dto.getPhone());
    }

    @Test
    public void testLongAddress() {
        AddressDTO dto = new AddressDTO();
        String longAddress = "A".repeat(500);
        dto.setAddressLine1(longAddress);
        assertEquals(500, dto.getAddressLine1().length());
    }

    @Test
    public void testNullAddressLine2() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLine2(null);
        assertNull(dto.getAddressLine2());
    }

    @Test
    public void testEmptyAddressLine2() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLine2("");
        assertEquals("", dto.getAddressLine2());
    }

    @Test
    public void testMultipleCities() {
        AddressDTO dto = new AddressDTO();
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston"};

        for (String city : cities) {
            dto.setCity(city);
            assertEquals(city, dto.getCity());
        }
    }

    @Test
    public void testUnicodeCity() {
        AddressDTO dto = new AddressDTO();
        dto.setCity("São Paulo");
        assertEquals("São Paulo", dto.getCity());
    }

    @Test
    public void testPostalCodeFormats() {
        AddressDTO dto = new AddressDTO();
        String[] postalCodes = {"10001", "90210", "12345-6789", "AB12 3CD"};

        for (String code : postalCodes) {
            dto.setPostalCode(code);
            assertEquals(code, dto.getPostalCode());
        }
    }

    @Test
    public void testAddressLabelOptions() {
        AddressDTO dto = new AddressDTO();
        String[] labels = {"Home", "Work", "Office", "Other"};

        for (String label : labels) {
            dto.setAddressLabel(label);
            assertEquals(label, dto.getAddressLabel());
        }
    }

    @Test
    public void testCompleteAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(1);
        dto.setAddressLabel("Home");
        dto.setFullName("John Doe");
        dto.setPhone("0771234567");
        dto.setAddressLine1("123 Main St");
        dto.setAddressLine2("Apt 4");
        dto.setCity("New York");
        dto.setPostalCode("10001");
        dto.setDefault(true);

        assertDoesNotThrow(dto::validate);
    }

    @Test
    public void testMinimalValidAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("A");
        dto.setPhone("1");
        dto.setAddressLine1("A");
        dto.setCity("A");
        dto.setPostalCode("1");

        assertDoesNotThrow(dto::validate);
    }

    @Test
    public void testMultipleAddresses() {
        AddressDTO dto1 = new AddressDTO();
        dto1.setAddressId(1);
        dto1.setAddressLabel("Home");

        AddressDTO dto2 = new AddressDTO();
        dto2.setAddressId(2);
        dto2.setAddressLabel("Work");

        assertNotEquals(dto1.getAddressId(), dto2.getAddressId());
        assertNotEquals(dto1.getAddressLabel(), dto2.getAddressLabel());
    }

    @Test
    public void testValidateWithAddressLine2() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setAddressLine2("Suite 100");
        dto.setCity("New York");
        dto.setPostalCode("10001");

        assertDoesNotThrow(dto::validate);
    }

    @Test
    public void testValidateWithoutAddressLine2() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        dto.setPostalCode("10001");

        assertDoesNotThrow(dto::validate);
    }

    @Test
    public void testAddressIdZero() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(0);
        assertEquals(0, dto.getAddressId());
    }

    @Test
    public void testAddressIdNegative() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(-1);
        assertEquals(-1, dto.getAddressId());
    }

    @Test
    public void testAddressIdLarge() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getAddressId());
    }

    @Test
    public void testNullAddressLabel() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLabel(null);
        assertNull(dto.getAddressLabel());
    }

    @Test
    public void testEmptyAddressLabel() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLabel("");
        assertEquals("", dto.getAddressLabel());
    }

    @Test
    public void testLongAddressLabel() {
        AddressDTO dto = new AddressDTO();
        String longLabel = "A".repeat(100);
        dto.setAddressLabel(longLabel);
        assertEquals(100, dto.getAddressLabel().length());
    }

    @Test
    public void testSpecialCharsInAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLine1("123 Main St., Apt #4B");
        assertEquals("123 Main St., Apt #4B", dto.getAddressLine1());
    }

    @Test
    public void testCityWithSpaces() {
        AddressDTO dto = new AddressDTO();
        dto.setCity("New York City");
        assertEquals("New York City", dto.getCity());
    }

    @Test
    public void testPostalCodeNumericOnly() {
        AddressDTO dto = new AddressDTO();
        dto.setPostalCode("12345");
        assertEquals("12345", dto.getPostalCode());
    }

    @Test
    public void testPostalCodeAlphanumeric() {
        AddressDTO dto = new AddressDTO();
        dto.setPostalCode("ABC123");
        assertEquals("ABC123", dto.getPostalCode());
    }

    @Test
    public void testPhoneNumericOnly() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("1234567890");
        assertEquals("1234567890", dto.getPhone());
    }

    @Test
    public void testLongPhone() {
        AddressDTO dto = new AddressDTO();
        String longPhone = "1".repeat(50);
        dto.setPhone(longPhone);
        assertEquals(50, dto.getPhone().length());
    }

    @Test
    public void testShortPhone() {
        AddressDTO dto = new AddressDTO();
        dto.setPhone("123");
        assertEquals("123", dto.getPhone());
    }

    @Test
    public void testAddressWithNumbers() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLine1("123 Main Street 456");
        assertEquals("123 Main Street 456", dto.getAddressLine1());
    }

    @Test
    public void testCityWithNumbers() {
        AddressDTO dto = new AddressDTO();
        dto.setCity("City123");
        assertEquals("City123", dto.getCity());
    }

    @Test
    public void testValidateMultipleTimes() {
        AddressDTO dto = new AddressDTO();
        dto.setFullName("John Doe");
        dto.setPhone("1234567890");
        dto.setAddressLine1("123 Main St");
        dto.setCity("New York");
        dto.setPostalCode("10001");

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(dto::validate);
        }
    }
}
