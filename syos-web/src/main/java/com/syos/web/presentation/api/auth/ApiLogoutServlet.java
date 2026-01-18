package com.syos.web.presentation.api.auth;

import com.google.gson.Gson;
import com.syos.web.concurrency.SessionManager;
import com.syos.web.concurrency.RequestLogger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/logout")
public class ApiLogoutServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setContentType("application/json; charset=UTF-8");

        HttpSession session = req.getSession(false);

        // Get userId FIRST before invalidating session
        String userId = null;
        String sessionId = null;

        if (session != null) {
            userId = (String) session.getAttribute("username");
            sessionId = (String) session.getAttribute("sessionId");
        }

        // NOW log with the userId (fixes "Request ID not found" error)
        String requestId = RequestLogger.logRequest("LOGOUT", userId, req.getRemoteAddr(), Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        try {
            if (session != null) {
                // Invalidate session in SessionManager
                if (sessionId != null) {
                    SessionManager.invalidateSession(sessionId);
                }

                // Invalidate HTTP session
                session.invalidate();
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of("ok", true)));

            // Log success
            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                    "ok", false,
                    "message", "Logout failed: " + e.getMessage()
            )));

            // Log error
            RequestLogger.updateStatus(requestId, "FAILED", startTime);
        }
    }

    private static void addCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}