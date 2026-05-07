package com.syos.web.presentation.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * CORS Filter - Enable Cross-Origin Resource Sharing
 * Allows frontend (localhost:5173) to communicate with backend (localhost:8081)
 * NOTE: Disabled @WebFilter to avoid duplicate with web.xml configuration
 */
// @WebFilter("/*")  // DISABLED - using web.xml configuration instead
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get origin from request
        String origin = httpRequest.getHeader("Origin");

        // Set CORS headers - simplified and more reliable
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Vary", "Origin");

        // Handle OPTIONS preflight requests immediately
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.getWriter().flush();
            return;
        }

        // Continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("✅ CORS Filter initialized - Frontend can now communicate with backend");
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}

