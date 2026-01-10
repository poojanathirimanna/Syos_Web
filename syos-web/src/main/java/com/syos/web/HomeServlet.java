package com.syos.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        // Safety check (filter should handle, but extra safe)
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("""
                <!DOCTYPE html>
                <html>
                <head><title>Home</title></head>
                <body>
                  <h2>Home ✅</h2>
                  <p>Welcome, %s</p>
                  <a href="logout">Logout</a>
                </body>
                </html>
                """.formatted(username));
    }
}
