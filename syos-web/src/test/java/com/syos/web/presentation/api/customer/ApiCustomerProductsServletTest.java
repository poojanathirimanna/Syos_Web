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

public class ApiCustomerProductsServletTest {

    private ApiCustomerProductsServlet servlet;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiCustomerProductsServlet();
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
    public void testDoGetWithCategoryFilter() throws Exception {
        when(mockRequest.getParameter("categoryId")).thenReturn("1");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithSearchQuery() throws Exception {
        when(mockRequest.getParameter("search")).thenReturn("laptop");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetMultipleTimes() throws Exception {
        for (int i = 0; i < 5; i++) {
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithDifferentCategories() throws Exception {
        String[] categories = {"1", "2", "3", "4", "5"};

        for (String category : categories) {
            when(mockRequest.getParameter("categoryId")).thenReturn(category);
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetVerifiesResponseWriter() throws Exception {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testMultipleServletInstances() {
        ApiCustomerProductsServlet servlet1 = new ApiCustomerProductsServlet();
        ApiCustomerProductsServlet servlet2 = new ApiCustomerProductsServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
    }

    @Test
    public void testDoGetWithNullCategory() throws Exception {
        when(mockRequest.getParameter("categoryId")).thenReturn(null);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithEmptySearch() throws Exception {
        when(mockRequest.getParameter("search")).thenReturn("");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithSpecialCharsInSearch() throws Exception {
        when(mockRequest.getParameter("search")).thenReturn("test & product");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }
}

