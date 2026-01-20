package com.syos.web.presentation.filters;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // ✅ Public paths (no authentication required)
        boolean isPublic =
                path.equals("/") ||
                        path.equals("/index.html") ||
                        path.equals("/api/login") ||
                        path.equals("/api/register") ||
                        path.equals("/api/google-login");

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("username") != null);

        if (!loggedIn) {
            // Return JSON 401 for all unauthorized API requests
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json; charset=UTF-8");

            // ✅ Use ApiResponse with "success" field
            ApiResponse<Object> errorResponse = ApiResponse.error("Not authenticated");
            resp.getWriter().write(gson.toJson(errorResponse));
            return;
        }

        // User is authenticated, continue with request
        chain.doFilter(request, response);
    }
}