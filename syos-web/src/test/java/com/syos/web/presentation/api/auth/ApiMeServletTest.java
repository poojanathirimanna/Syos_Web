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

public class ApiMeServletTest {

    private ApiMeServlet servlet;

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
        servlet = new ApiMeServlet();
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
        when(mockSession.getAttribute("username")).thenReturn("test@test.com");
        when(mockSession.getAttribute("userId")).thenReturn("U001");
        when(mockSession.getAttribute("roleId")).thenReturn(3);

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
    public void testDoGetWithNullUsername() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn(null);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetSetsContentType() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("test@test.com");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetMultipleTimes() throws Exception {
        for (int i = 0; i < 5; i++) {
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("username")).thenReturn("test" + i + "@test.com");
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithDifferentRoles() throws Exception {
        Integer[] roles = {1, 2, 3};

        for (Integer role : roles) {
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("username")).thenReturn("test@test.com");
            when(mockSession.getAttribute("roleId")).thenReturn(role);

            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(3)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithAdminRole() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("admin@test.com");
        when(mockSession.getAttribute("roleId")).thenReturn(1);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithCashierRole() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("cashier@test.com");
        when(mockSession.getAttribute("roleId")).thenReturn(2);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithCustomerRole() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("customer@test.com");
        when(mockSession.getAttribute("roleId")).thenReturn(3);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetVerifiesResponseWriter() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("test@test.com");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
    }

    @Test
    public void testMultipleServletInstances() {
        ApiMeServlet servlet1 = new ApiMeServlet();
        ApiMeServlet servlet2 = new ApiMeServlet();
        ApiMeServlet servlet3 = new ApiMeServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
        assertNotNull(servlet3);
    }

    @Test
    public void testDoGetWithDifferentUserIds() throws Exception {
        String[] userIds = {"U001", "U002", "C001", "A001"};

        for (String userId : userIds) {
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("username")).thenReturn("test@test.com");
            when(mockSession.getAttribute("userId")).thenReturn(userId);

            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(4)).setContentType("application/json; charset=UTF-8");
    }
}

