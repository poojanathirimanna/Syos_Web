package com.syos.web.presentation.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthFilterTest {

    private AuthFilter authFilter;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private HttpSession mockSession;

    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        authFilter = new AuthFilter();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testConstructor() {
        assertNotNull(authFilter);
    }

    @Test
    public void testDoFilterWithPublicPathRoot() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPublicPathIndexHtml() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/index.html");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPublicPathLogin() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/login");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPublicPathRegister() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/register");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPublicPathGoogleLogin() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/google-login");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithAuthenticatedUser() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/cart");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn("testuser");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithUnauthenticatedUser() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/cart");
        when(mockRequest.getSession(false)).thenReturn(null);

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithNullSession() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/products");
        when(mockRequest.getSession(false)).thenReturn(null);

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithSessionButNoUsername() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/products");
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("username")).thenReturn(null);

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithContextPath() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("/myapp");
        when(mockRequest.getRequestURI()).thenReturn("/myapp/api/login");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterPublicPathsAllowAccess() throws IOException, ServletException {
        String[] publicPaths = {"/", "/index.html", "/api/login", "/api/register", "/api/google-login"};

        for (String path : publicPaths) {
            when(mockRequest.getContextPath()).thenReturn("");
            when(mockRequest.getRequestURI()).thenReturn(path);

            authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        }

        verify(mockFilterChain, times(publicPaths.length)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterProtectedPathsRequireAuth() throws IOException, ServletException {
        String[] protectedPaths = {"/api/cart", "/api/products", "/api/wishlist", "/api/addresses"};

        for (String path : protectedPaths) {
            when(mockRequest.getContextPath()).thenReturn("");
            when(mockRequest.getRequestURI()).thenReturn(path);
            when(mockRequest.getSession(false)).thenReturn(null);

            authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        }

        verify(mockResponse, times(protectedPaths.length)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterReturnsJsonOnUnauthorized() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/cart");
        when(mockRequest.getSession(false)).thenReturn(null);

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        String response = responseWriter.toString();
        assertTrue(response.contains("success") || response.contains("Not authenticated"));
    }

    @Test
    public void testMultipleFilterCalls() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/login");

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, times(3)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testAuthFilterWithDifferentUsernames() throws IOException, ServletException {
        String[] usernames = {"user1", "admin", "customer@test.com"};

        for (String username : usernames) {
            when(mockRequest.getContextPath()).thenReturn("");
            when(mockRequest.getRequestURI()).thenReturn("/api/products");
            when(mockRequest.getSession(false)).thenReturn(mockSession);
            when(mockSession.getAttribute("username")).thenReturn(username);

            authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        }

        verify(mockFilterChain, times(usernames.length)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testAuthFilterDoesNotChainForUnauthorized() throws IOException, ServletException {
        when(mockRequest.getContextPath()).thenReturn("");
        when(mockRequest.getRequestURI()).thenReturn("/api/protected");
        when(mockRequest.getSession(false)).thenReturn(null);

        authFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    @Test
    public void testMultipleAuthFilters() {
        AuthFilter filter1 = new AuthFilter();
        AuthFilter filter2 = new AuthFilter();
        AuthFilter filter3 = new AuthFilter();

        assertNotNull(filter1);
        assertNotNull(filter2);
        assertNotNull(filter3);
    }
}

