package com.syos.web.presentation.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CorsFilterTest {

    private CorsFilter corsFilter;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private FilterConfig mockFilterConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        corsFilter = new CorsFilter();
    }

    @Test
    public void testConstructor() {
        assertNotNull(corsFilter);
    }

    @Test
    public void testInit() {
        assertDoesNotThrow(() -> corsFilter.init(mockFilterConfig));
    }

    @Test
    public void testDestroy() {
        assertDoesNotThrow(() -> corsFilter.destroy());
    }

    @Test
    public void testDoFilterSetsAccessControlAllowOrigin() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
    }

    @Test
    public void testDoFilterSetsAccessControlAllowCredentials() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    public void testDoFilterSetsAccessControlAllowMethods() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    @Test
    public void testDoFilterSetsAccessControlAllowHeaders() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
    }

    @Test
    public void testDoFilterSetsAccessControlMaxAge() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Access-Control-Max-Age", "3600");
    }

    @Test
    public void testDoFilterSetsVaryHeader() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader("Vary", "Origin");
    }

    @Test
    public void testDoFilterWithGETMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPOSTMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("POST");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithPUTMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("PUT");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithDELETEMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("DELETE");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithOPTIONSMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("OPTIONS");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithOPTIONSMethodLowerCase() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("options");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterWithOPTIONSMethodMixedCase() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("Options");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockFilterChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterSetsAllHeaders() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(6)).setHeader(anyString(), anyString());
    }

    @Test
    public void testMultipleFilterCalls() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, times(3)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testInitWithNullConfig() {
        assertDoesNotThrow(() -> corsFilter.init(null));
    }

    @Test
    public void testDestroyMultipleTimes() {
        assertDoesNotThrow(() -> {
            corsFilter.destroy();
            corsFilter.destroy();
            corsFilter.destroy();
        });
    }

    @Test
    public void testFilterLifecycle() throws IOException, ServletException {
        corsFilter.init(mockFilterConfig);

        when(mockRequest.getMethod()).thenReturn("GET");
        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        corsFilter.destroy();

        assertNotNull(corsFilter);
    }

    @Test
    public void testDoFilterWithNullMethod() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn(null);

        assertDoesNotThrow(() -> corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain));
    }

    @Test
    public void testDoFilterChainsCorrectly() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testDoFilterDoesNotChainForOPTIONS() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("OPTIONS");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, never()).doFilter(any(), any());
    }

    @Test
    public void testAllHttpMethods() throws IOException, ServletException {
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"};

        for (String method : methods) {
            when(mockRequest.getMethod()).thenReturn(method);
            corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        }

        verify(mockFilterChain, times(methods.length)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCorsHeaderValues() throws IOException, ServletException {
        when(mockRequest.getMethod()).thenReturn("GET");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setHeader(eq("Access-Control-Allow-Origin"), eq("http://localhost:5173"));
        verify(mockResponse).setHeader(eq("Access-Control-Allow-Credentials"), eq("true"));
        verify(mockResponse).setHeader(eq("Access-Control-Max-Age"), eq("3600"));
    }

    @Test
    public void testMultipleFiltersCanBeCreated() {
        CorsFilter filter1 = new CorsFilter();
        CorsFilter filter2 = new CorsFilter();
        CorsFilter filter3 = new CorsFilter();

        assertNotNull(filter1);
        assertNotNull(filter2);
        assertNotNull(filter3);
    }
}

