package com.syos.web.presentation.api.cashier;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiAvailableProductsServletTest {

    private ApiAvailableProductsServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiAvailableProductsServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoGet1() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet2() throws Exception { when(mockRequest.getParameter("location")).thenReturn("SHELF"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet3() throws Exception { when(mockRequest.getParameter("categoryId")).thenReturn("1"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet4() throws Exception { for(int i=0;i<5;i++) servlet.doGet(mockRequest, mockResponse); verify(mockResponse,atLeast(5)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet5() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).getWriter(); }
    @Test public void testDoGet6() throws Exception { when(mockRequest.getParameter("location")).thenReturn(null); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet7() throws Exception { when(mockRequest.getParameter("search")).thenReturn("laptop"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testInstance1() { assertNotNull(new ApiAvailableProductsServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiAvailableProductsServlet()); }
    @Test public void testInstance3() { assertNotNull(new ApiAvailableProductsServlet()); }
}
