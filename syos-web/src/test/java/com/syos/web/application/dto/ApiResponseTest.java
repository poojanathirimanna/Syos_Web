
package com.syos.web.application.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    private ApiResponse<String> apiResponse;

    @BeforeEach
    public void setUp() {
        apiResponse = new ApiResponse<>();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(apiResponse);
    }

    @Test
    public void testSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success("Data");
        assertTrue(response.isSuccess());
        assertEquals("Data", response.getData());
    }

    @Test
    public void testErrorResponse() {
        ApiResponse<String> response = ApiResponse.error("Error message");
        assertFalse(response.isSuccess());
        assertEquals("Error message", response.getMessage());
    }

    @Test
    public void testSetAndGetSuccess() {
        apiResponse.setSuccess(true);
        assertTrue(apiResponse.isSuccess());
    }

    @Test
    public void testSetAndGetData() {
        apiResponse.setData("Test data");
        assertEquals("Test data", apiResponse.getData());
    }

    @Test
    public void testSetAndGetMessage() {
        apiResponse.setMessage("Test message");
        assertEquals("Test message", apiResponse.getMessage());
    }

    @Test
    public void testNullData() {
        apiResponse.setData(null);
        assertNull(apiResponse.getData());
    }

    @Test
    public void testNullMessage() {
        apiResponse.setMessage(null);
        assertNull(apiResponse.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        apiResponse.setMessage("");
        assertEquals("", apiResponse.getMessage());
    }

    @Test
    public void testWhitespaceMessage() {
        apiResponse.setMessage("   ");
        assertEquals("   ", apiResponse.getMessage());
    }

    @Test
    public void testLongMessage() {
        String longMessage = "A".repeat(1000);
        apiResponse.setMessage(longMessage);
        assertEquals(1000, apiResponse.getMessage().length());
    }

    @Test
    public void testSpecialCharactersInMessage() {
        apiResponse.setMessage("Error: @#$%^&*()");
        assertEquals("Error: @#$%^&*()", apiResponse.getMessage());
    }

    @Test
    public void testUnicodeInMessage() {
        apiResponse.setMessage("Mensaje de error 错误消息");
        assertEquals("Mensaje de error 错误消息", apiResponse.getMessage());
    }

    @Test
    public void testSuccessFalse() {
        apiResponse.setSuccess(false);
        assertFalse(apiResponse.isSuccess());
    }

    @Test
    public void testCompleteSuccessScenario() {
        apiResponse.setSuccess(true);
        apiResponse.setData("User created");
        apiResponse.setMessage("Success");

        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getData());
        assertEquals("Success", apiResponse.getMessage());
    }

    @Test
    public void testCompleteErrorScenario() {
        apiResponse.setSuccess(false);
        apiResponse.setData(null);
        apiResponse.setMessage("User not found");

        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getData());
        assertNotNull(apiResponse.getMessage());
    }

    @Test
    public void testWithIntegerData() {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(42);

        assertEquals(42, response.getData());
    }

    @Test
    public void testWithListData() {
        ApiResponse<java.util.List<String>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(java.util.Arrays.asList("Item1", "Item2"));

        assertEquals(2, response.getData().size());
    }

    @Test
    public void testWithNullInSuccessMethod() {
        ApiResponse<String> response = ApiResponse.success(null);
        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }
}

