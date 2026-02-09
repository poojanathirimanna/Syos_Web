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
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get origin from request
        String origin = httpRequest.getHeader("Origin");

        // Allow requests from your frontend
        if (origin != null && (origin.equals("http://localhost:5173") ||
                               origin.equals("http://localhost:3000") ||
                               origin.equals("http://localhost:5174"))) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        }

        // Allow credentials (cookies)
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Allow common HTTP methods
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Allow common headers
        httpResponse.setHeader("Access-Control-Allow-Headers",
            "Content-Type, Authorization, X-Requested-With, Accept, Origin");

        // Cache preflight response for 1 hour
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Debug: Log session info
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            System.out.println("🔍 CORS Filter - Session ID: " + session.getId() +
                             " | Username: " + username +
                             " | Path: " + httpRequest.getRequestURI());
        } else {
            System.out.println("⚠️ CORS Filter - No session found for path: " + httpRequest.getRequestURI());
        }

        // Handle OPTIONS preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
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

