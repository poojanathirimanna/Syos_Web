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

public class ApiCategoriesServletTest {

    private ApiCategoriesServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiCategoriesServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoGet1() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet2() throws Exception { for(int i=0;i<5;i++) servlet.doGet(mockRequest, mockResponse); verify(mockResponse,atLeast(5)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoGet3() throws Exception { servlet.doGet(mockRequest, mockResponse); verify(mockResponse).getWriter(); }
    @Test public void testDoPost1() throws Exception { String json = "{\"categoryName\":\"Electronics\"}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost2() throws Exception { String json = "{\"categoryName\":null}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost3() throws Exception { String json = "{}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost4() throws Exception { String json = "{invalid}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost5() throws Exception { for(int i=1;i<=3;i++) { String json = "{\"categoryName\":\"Category"+i+"\"}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPost(mockRequest, mockResponse); } verify(mockResponse,atLeast(3)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPut1() throws Exception { String json = "{\"categoryId\":1,\"categoryName\":\"Updated\"}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPut(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPut2() throws Exception { String json = "{\"categoryId\":null}"; when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json))); servlet.doPut(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete1() throws Exception { when(mockRequest.getParameter("categoryId")).thenReturn("1"); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoDelete2() throws Exception { when(mockRequest.getParameter("categoryId")).thenReturn(null); servlet.doDelete(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testInstance1() { assertNotNull(new ApiCategoriesServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiCategoriesServlet()); }
}

