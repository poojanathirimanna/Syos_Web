package com.syos.web;

import com.syos.web.dao.UserDao;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RegisterServlet extends HttpServlet {

    private static final int DEFAULT_ROLE_ID = 2; // change if your cashier role_id differs

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String userId = trim(req.getParameter("user_id"));
        String fullName = trim(req.getParameter("full_name"));
        String contactNumber = trim(req.getParameter("contact_number"));
        String email = trim(req.getParameter("email"));
        String password = req.getParameter("password");

        if (isBlank(userId) || isBlank(fullName) || isBlank(email) || isBlank(password)) {
            redirectWithError(resp, "All fields are required.");
            return;
        }

        UserDao dao = new UserDao();

        if (dao.existsByUserId(userId)) {
            redirectWithError(resp, "User ID already exists. Please choose another.");
            return;
        }

        if (dao.existsByEmail(email)) {
            redirectWithError(resp, "Email already exists. Please use another email.");
            return;
        }

        boolean ok = dao.registerUser(userId, fullName, contactNumber ,  email, password, DEFAULT_ROLE_ID);

        if (ok) {
            resp.sendRedirect("login.html?msg=" + url("Account created successfully. Please login."));
        } else {
            redirectWithError(resp, "Registration failed. Please try again.");
        }
    }

    private void redirectWithError(HttpServletResponse resp, String message) throws IOException {
        resp.sendRedirect("register.html?error=" + url(message));
    }

    private String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
