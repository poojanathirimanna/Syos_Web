package com.syos.web.presentation.api.customer;

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

public class ApiCartServletTest {

    private ApiCartServlet servlet;

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
        servlet = new ApiCartServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    public void testDoGetWithValidSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithNullSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(null);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostAddToCart() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"quantity\":5}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullProductCode() throws Exception {
        String jsonInput = "{\"productCode\":null,\"quantity\":5}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithZeroQuantity() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"quantity\":0}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNegativeQuantity() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"quantity\":-5}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithLargeQuantity() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"quantity\":1000}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPutUpdateCartItem() throws Exception {
        String jsonInput = "{\"cartId\":1,\"quantity\":10}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoDeleteRemoveCartItem() throws Exception {
        when(mockRequest.getParameter("cartId")).thenReturn("1");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoDeleteWithNullCartId() throws Exception {
        when(mockRequest.getParameter("cartId")).thenReturn(null);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostMultipleProducts() throws Exception {
        String[] products = {"P001", "P002", "P003"};

        for (String product : products) {
            String jsonInput = "{\"productCode\":\"" + product + "\",\"quantity\":5}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("userId")).thenReturn("U001");

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(3)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithInvalidJson() throws Exception {
        String jsonInput = "{invalid}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetMultipleTimes() throws Exception {
        for (int i = 0; i < 5; i++) {
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("userId")).thenReturn("U00" + i);

            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testMultipleServletInstances() {
        ApiCartServlet servlet1 = new ApiCartServlet();
        ApiCartServlet servlet2 = new ApiCartServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
    }

    @Test
    public void testDoPostVerifiesResponseWriter() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"quantity\":5}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoGetVerifiesResponseWriter() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("U001");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoPostWithDifferentQuantities() throws Exception {
        int[] quantities = {1, 5, 10, 50, 100};

        for (int qty : quantities) {
            String jsonInput = "{\"productCode\":\"P001\",\"quantity\":" + qty + "}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("userId")).thenReturn("U001");

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }
}

