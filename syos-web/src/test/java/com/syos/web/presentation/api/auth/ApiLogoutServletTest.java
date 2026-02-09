package com.syos.web.presentation.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiLogoutServletTest {

    private ApiLogoutServlet servlet;

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
        servlet = new ApiLogoutServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    public void testDoPostWithValidSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockSession).invalidate();
        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostWithNullSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(null);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoPostInvalidatesSession() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockSession, times(1)).invalidate();
    }

    @Test
    public void testDoPostMultipleTimes() throws Exception {
        for (int i = 0; i < 5; i++) {
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            servlet.doPost(mockRequest, mockResponse);
        }

        verify(mockSession, atLeast(5)).invalidate();
    }

    @Test
    public void testMultipleServletInstances() {
        ApiLogoutServlet servlet1 = new ApiLogoutServlet();
        ApiLogoutServlet servlet2 = new ApiLogoutServlet();
        ApiLogoutServlet servlet3 = new ApiLogoutServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
        assertNotNull(servlet3);
    }

    @Test
    public void testDoPostVerifiesResponseWriter() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoPostSetsContentType() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        servlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }
}

