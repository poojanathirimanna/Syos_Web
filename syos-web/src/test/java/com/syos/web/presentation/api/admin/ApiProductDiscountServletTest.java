package com.syos.web.presentation.api.admin;

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

public class ApiProductDiscountServletTest {

    private ApiProductDiscountServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiProductDiscountServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoPost1() throws Exception { String json = "{\"productCode\":\"P001\",\"discountPercentage\":10,\"startDate\":\"2024-01-01\",\"endDate\":\"2024-12-31\"}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost2() throws Exception { String json = "{\"productCode\":null}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost3() throws Exception { String json = "{\"productCode\":\"P001\",\"discountPercentage\":0}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost4() throws Exception { String json = "{\"productCode\":\"P001\",\"discountPercentage\":50}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost5() throws Exception { String json = "{}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete1() throws Exception { when(mockRequest.getParameter("productCode")).thenReturn("P001"); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete2() throws Exception { when(mockRequest.getParameter("productCode")).thenReturn(null); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testInstance1() { assertNotNull(new ApiProductDiscountServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiProductDiscountServlet()); }
}

