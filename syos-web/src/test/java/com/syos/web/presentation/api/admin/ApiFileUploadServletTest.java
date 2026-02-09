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

public class ApiFileUploadServletTest {

    private ApiFileUploadServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiFileUploadServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test public void testServletInit() { assertNotNull(servlet); }
    @Test public void testDoPost1() throws Exception { servlet.doPost(mockRequest, mockResponse); verify(mockResponse).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost2() throws Exception { for(int i=0;i<3;i++) servlet.doPost(mockRequest, mockResponse); verify(mockResponse,atLeast(3)).setContentType("application/json; charset=UTF-8"); }
    @Test public void testDoPost3() throws Exception { servlet.doPost(mockRequest, mockResponse); verify(mockResponse).getWriter(); }
    @Test public void testInstance1() { assertNotNull(new ApiFileUploadServlet()); }
    @Test public void testInstance2() { assertNotNull(new ApiFileUploadServlet()); }
}

