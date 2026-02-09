package com.syos.web.presentation.api.admin.products;

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

public class ApiProductsServletTest {

    private ApiProductsServlet servlet;

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
        servlet = new ApiProductsServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    public void testDoGetAllProducts() throws Exception {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostCreateProduct() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Test Product\",\"unitPrice\":100.00,\"categoryId\":1}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithMissingFields() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPutUpdateProduct() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Updated Product\",\"unitPrice\":150.00}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoDeleteProduct() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn("P001");
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullProductCode() throws Exception {
        String jsonInput = "{\"productCode\":null,\"name\":\"Test\",\"unitPrice\":100.00}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithZeroPrice() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Test\",\"unitPrice\":0}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNegativePrice() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Test\",\"unitPrice\":-100}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostMultipleProducts() throws Exception {
        for (int i = 1; i <= 5; i++) {
            String jsonInput = "{\"productCode\":\"P00" + i + "\",\"name\":\"Product " + i + "\",\"unitPrice\":" + (i * 100) + "}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);
            when(mockRequest.getSession(false)).thenReturn(mockSession);

            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetMultipleTimes() throws Exception {
        for (int i = 0; i < 5; i++) {
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testMultipleServletInstances() {
        ApiProductsServlet servlet1 = new ApiProductsServlet();
        ApiProductsServlet servlet2 = new ApiProductsServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
    }

    @Test
    public void testDoPostWithInvalidJson() throws Exception {
        String jsonInput = "{invalid}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoDeleteWithNullProductCode() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn(null);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostVerifiesResponseWriter() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Test\",\"unitPrice\":100}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoGetVerifiesResponseWriter() throws Exception {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoPostWithLongProductName() throws Exception {
        String longName = "A".repeat(200);
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"" + longName + "\",\"unitPrice\":100}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithSpecialCharsInName() throws Exception {
        String jsonInput = "{\"productCode\":\"P001\",\"name\":\"Test & Product (50%)\",\"unitPrice\":100}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));

        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPutMultipleTimes() throws Exception {
        for (int i = 1; i <= 3; i++) {
            String jsonInput = "{\"productCode\":\"P00" + i + "\",\"name\":\"Updated " + i + "\",\"unitPrice\":" + (i * 100) + "}";
            BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
            when(mockRequest.getReader()).thenReturn(reader);
            when(mockRequest.getSession(false)).thenReturn(mockSession);

            servlet.doPut(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(3)).setContentType("application/json; charset=UTF-8");
    }
}

