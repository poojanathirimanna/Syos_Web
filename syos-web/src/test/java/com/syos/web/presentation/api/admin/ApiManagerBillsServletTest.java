package com.syos.web.presentation.api.admin;

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

public class ApiManagerBillsServletTest {

    private ApiManagerBillsServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiManagerBillsServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoGet1() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet2() throws Exception { when(mockRequest.getParameter("userId")).thenReturn("U001"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet3() throws Exception { when(mockRequest.getParameter("startDate")).thenReturn("2024-01-01"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet4() throws Exception { for(int i=0;i<5;i++) servlet.doGet(mockRequest, mockResponse); verify(mockResponse,atLeast(5)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet5() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).getWriter(); }
    @Test public void testInstance1() { assertNotNull(new ApiManagerBillsServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiManagerBillsServlet()); }
}

