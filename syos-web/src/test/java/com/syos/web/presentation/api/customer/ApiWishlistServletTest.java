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

public class ApiWishlistServletTest {

    private ApiWishlistServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpSession mockSession;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiWishlistServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoGet1() throws Exception { when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet2() throws Exception { when(mockRequest.getSession(false)).thenReturn(null); servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet3() throws Exception { for(int i=0;i<5;i++) { when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U00"+i); servlet.doGet(mockRequest, mockResponse); } verify(mockResponse,atLeast(5)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost1() throws Exception { String json = "{\"productCode\":\"P001\"}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost2() throws Exception { String json = "{\"productCode\":null}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost3() throws Exception { String json = "{}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete1() throws Exception { when(mockRequest.getParameter("productCode")).thenReturn("P001"); when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete2() throws Exception { when(mockRequest.getParameter("productCode")).thenReturn(null); when(mockRequest.getSession(false)).thenReturn(mockSession); when(mockSession.getAttribute("userId")).thenReturn("U001"); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testInstance1() { assertNotNull(new ApiWishlistServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiWishlistServlet()); }
}


