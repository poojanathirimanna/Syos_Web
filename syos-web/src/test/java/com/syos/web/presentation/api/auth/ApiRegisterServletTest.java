package com.syos.web.presentation.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiRegisterServletTest {

    private ApiRegisterServlet servlet;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiRegisterServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    public void testDoPostWithCompleteData() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMissingUserId() throws Exception {
        String jsonInput = "{\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMissingFullName() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMissingEmail() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMissingPassword() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithInvalidEmail() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"invalidemail\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithShortPassword() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"12\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithInvalidJson() throws Exception {
        String jsonInput = "{invalid}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithEmptyJson() throws Exception {
        String jsonInput = "{}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullValues() throws Exception {
        String jsonInput = "{\"userId\":null,\"fullName\":null,\"contactNumber\":null,\"email\":null,\"password\":null}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testMultipleServletInstances() {
        ApiRegisterServlet servlet1 = new ApiRegisterServlet();
        ApiRegisterServlet servlet2 = new ApiRegisterServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
    }

    @Test
    public void testDoPostWithSpecialCharsInName() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"O'Brien-Smith\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithLongFullName() throws Exception {
        String longName = "A".repeat(200);
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"" + longName + "\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithInternationalPhone() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"+94771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithDifferentEmailFormats() throws Exception {
        String[] emails = {"user@test.com", "user.name@test.com", "user+tag@test.co.uk"};

        for (String email : emails) {
            String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"" + email + "\",\"password\":\"password123\"}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(3)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostMultipleTimes() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";

        for (int i = 0; i < 5; i++) {
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithEmptyStrings() throws Exception {
        String jsonInput = "{\"userId\":\"\",\"fullName\":\"\",\"contactNumber\":\"\",\"email\":\"\",\"password\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostVerifiesResponseWriter() throws Exception {
        String jsonInput = "{\"userId\":\"U001\",\"fullName\":\"John Doe\",\"contactNumber\":\"0771234567\",\"email\":\"john@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }
}

