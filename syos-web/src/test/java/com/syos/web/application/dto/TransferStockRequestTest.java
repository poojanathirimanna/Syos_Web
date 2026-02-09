package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class TransferStockRequestTest {

    private TransferStockRequest dto;

    @BeforeEach
    public void setUp() {
        dto = new TransferStockRequest();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
    }

    @Test
    public void testFullConstructor() {
        TransferStockRequest request = new TransferStockRequest("P001", 100, "MAIN", "SHELF", "U001");
        assertEquals("P001", request.getProductCode());
        assertEquals(100, request.getQuantity());
        assertEquals("MAIN", request.getFromLocation());
        assertEquals("SHELF", request.getToLocation());
        assertEquals("U001", request.getUserId());
    }

    @Test
    public void testSetAndGetProductCode() {
        dto.setProductCode("P001");
        assertEquals("P001", dto.getProductCode());
    }

    @Test
    public void testSetAndGetQuantity() {
        dto.setQuantity(50);
        assertEquals(50, dto.getQuantity());
    }

    @Test
    public void testSetAndGetFromLocation() {
        dto.setFromLocation("MAIN");
        assertEquals("MAIN", dto.getFromLocation());
    }

    @Test
    public void testSetAndGetToLocation() {
        dto.setToLocation("SHELF");
        assertEquals("SHELF", dto.getToLocation());
    }

    @Test
    public void testSetAndGetUserId() {
        dto.setUserId("U001");
        assertEquals("U001", dto.getUserId());
    }

    @Test
    public void testValidateSuccess() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMainToShelf() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMainToWebsite() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("WEBSITE");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateShelfToMain() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("SHELF");
        dto.setToLocation("MAIN");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateShelfToWebsite() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("SHELF");
        dto.setToLocation("WEBSITE");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWebsiteToMain() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("WEBSITE");
        dto.setToLocation("MAIN");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateWebsiteToShelf() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("WEBSITE");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateNullProductCode() {
        dto.setProductCode(null);
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyProductCode() {
        dto.setProductCode("");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateWhitespaceProductCode() {
        dto.setProductCode("   ");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateZeroQuantity() {
        dto.setProductCode("P001");
        dto.setQuantity(0);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNegativeQuantity() {
        dto.setProductCode("P001");
        dto.setQuantity(-10);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullFromLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation(null);
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyFromLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateNullToLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation(null);
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateEmptyToLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateInvalidFromLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("INVALID");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateInvalidToLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("INVALID");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateSameLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("MAIN");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateSameLocationShelf() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("SHELF");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateSameLocationWebsite() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("WEBSITE");
        dto.setToLocation("WEBSITE");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testValidateLowercaseLocations() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("main");
        dto.setToLocation("shelf");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateMixedCaseLocations() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("Main");
        dto.setToLocation("Shelf");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testValidateCaseInsensitiveSameLocation() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("main");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testNullValues() {
        dto.setProductCode(null);
        dto.setFromLocation(null);
        dto.setToLocation(null);
        dto.setUserId(null);

        assertNull(dto.getProductCode());
        assertNull(dto.getFromLocation());
        assertNull(dto.getToLocation());
        assertNull(dto.getUserId());
    }

    @Test
    public void testQuantityOne() {
        dto.setProductCode("P001");
        dto.setQuantity(1);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testLargeQuantity() {
        dto.setProductCode("P001");
        dto.setQuantity(100000);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testLongProductCode() {
        String longCode = "P" + "0".repeat(100);
        dto.setProductCode(longCode);
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testProductCodeWithSpecialChars() {
        dto.setProductCode("P-001_A");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testCompleteScenario() {
        dto.setProductCode("PROD001");
        dto.setQuantity(500);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");
        dto.setUserId("ADMIN001");

        assertNotNull(dto.getProductCode());
        assertTrue(dto.getQuantity() > 0);
        assertNotNull(dto.getFromLocation());
        assertNotNull(dto.getToLocation());
        assertNotNull(dto.getUserId());
        assertDoesNotThrow(() -> dto.validate());
    }

    @Test
    public void testAllLocationCombinations() {
        String[] locations = {"MAIN", "SHELF", "WEBSITE"};

        for (String from : locations) {
            for (String to : locations) {
                dto.setProductCode("P001");
                dto.setQuantity(100);
                dto.setFromLocation(from);
                dto.setToLocation(to);

                if (from.equals(to)) {
                    assertThrows(IllegalArgumentException.class, () -> dto.validate());
                } else {
                    assertDoesNotThrow(() -> dto.validate());
                }
            }
        }
    }

    @Test
    public void testLongUserId() {
        String longUserId = "U" + "0".repeat(100);
        dto.setUserId(longUserId);
        assertEquals(longUserId, dto.getUserId());
    }

    @Test
    public void testWhitespaceLocations() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("   ");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testLocationWithSpaces() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN STORE");
        dto.setToLocation("SHELF");
        assertThrows(IllegalArgumentException.class, () -> dto.validate());
    }

    @Test
    public void testMultipleValidations() {
        dto.setProductCode("P001");
        dto.setQuantity(100);
        dto.setFromLocation("MAIN");
        dto.setToLocation("SHELF");

        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> dto.validate());
        }
    }

    @Test
    public void testQuantityBoundary() {
        dto.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dto.getQuantity());
    }
}

