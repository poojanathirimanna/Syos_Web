package com.syos.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // ✅ public paths (no login required)
        boolean isPublic =
                path.equals("/login.html") ||
                        path.equals("/register.html") ||
                        path.equals("/login") ||
                        path.equals("/register") ||
                        path.equals("/api/login") ||
                        path.equals("/api/register") ||
                        path.startsWith("/assets/") ||   // optional (if you have assets)
                        path.equals("/") ;               // optional

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("username") != null);

        if (!loggedIn) {
            // ✅ For React/API calls: return JSON 401 (no redirect)
            if (path.startsWith("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write("{\"ok\":false,\"message\":\"Not authenticated\"}");
                return;
            }

            // ✅ For normal browser pages: redirect to login.html
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        chain.doFilter(request, response);
    }
}
