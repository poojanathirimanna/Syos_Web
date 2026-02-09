package com.syos.web.presentation.api.auth;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

public class ApiLoginServletTest {

    private ApiLoginServlet servlet;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpSession mockSession;

    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiLoginServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    public void testDoPostWithValidCredentials() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(true)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullUsername() throws Exception {
        String jsonInput = "{\"username\":null,\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithEmptyUsername() throws Exception {
        String jsonInput = "{\"username\":\"\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullPassword() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":null}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithEmptyPassword() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithInvalidJson() throws Exception {
        String jsonInput = "{invalid json}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMalformedJson() throws Exception {
        String jsonInput = "not json at all";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostSetsContentType() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostMultipleTimes() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"password123\"}";

        for (int i = 0; i < 5; i++) {
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testMultipleServletInstances() {
        ApiLoginServlet servlet1 = new ApiLoginServlet();
        ApiLoginServlet servlet2 = new ApiLoginServlet();
        ApiLoginServlet servlet3 = new ApiLoginServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
        assertNotNull(servlet3);
    }

    @Test
    public void testDoPostWithDifferentUsernames() throws Exception {
        String[] usernames = {"user1@test.com", "admin@test.com", "customer@test.com"};

        for (String username : usernames) {
            String jsonInput = "{\"username\":\"" + username + "\",\"password\":\"password123\"}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(3)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithSpecialCharactersInPassword() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"P@ssw0rd!123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithLongPassword() throws Exception {
        String longPassword = "a".repeat(100);
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"" + longPassword + "\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithShortPassword() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"12\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNumericPassword() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"123456\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostVerifiesResponseWriter() throws Exception {
        String jsonInput = "{\"username\":\"test@test.com\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }
}

