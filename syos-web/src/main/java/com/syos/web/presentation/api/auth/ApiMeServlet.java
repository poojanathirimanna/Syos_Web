package com.syos.web.presentation.api.auth;

import com.google.gson.Gson;
import com.syos.web.concurrency.SessionManager;  // 🆕 ADD
import com.syos.web.concurrency.RequestLogger;   // 🆕 ADD

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/me")
public class ApiMeServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setContentType("application/json; charset=UTF-8");

        HttpSession session = req.getSession(false);
        String userId = session != null ? (String) session.getAttribute("username") : null;

        // 🆕 ADD: Log request
        String requestId = RequestLogger.logRequest("CHECK_AUTH", userId, req.getRemoteAddr());
        long startTime = System.currentTimeMillis();

        try {
            if (session == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson(Map.of("loggedIn", false)));

                // 🆕 ADD: Log (not really a failure, just not logged in)
                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
                return;
            }

            String sessionId = (String) session.getAttribute("sessionId");
            String username = (String) session.getAttribute("username");

            // 🆕 ADD: Verify session is still valid
            if (sessionId != null) {
                SessionManager.SessionData sessionData = SessionManager.getSession(sessionId);
                if (sessionData == null) {
                    // Session expired
                    session.invalidate();
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(gson.toJson(Map.of("loggedIn", false)));

                    RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
                    return;
                }
            }

            if (username == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson(Map.of("loggedIn", false)));

                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
                return;
            }

            // Get user info from session
            String fullName = (String) session.getAttribute("fullName");
            Integer roleId = (Integer) session.getAttribute("roleId");

            if (fullName == null) fullName = username;
            if (roleId == null) roleId = 3;

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of(
                    "loggedIn", true,
                    "username", username,
                    "fullName", fullName,
                    "roleId", roleId
            )));

            // 🆕 ADD: Log success
            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                    "loggedIn", false,
                    "error", e.getMessage()
            )));

            // 🆕 ADD: Log error
            RequestLogger.updateStatus(requestId, "FAILED", startTime);
        }
    }

    private static void addCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}