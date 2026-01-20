package com.syos.web.presentation.api.auth;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.LoginRequest;
import com.syos.web.application.dto.UserDTO;
import com.syos.web.application.usecases.LoginUseCase;
import com.syos.web.concurrency.SessionManager;
import com.syos.web.concurrency.RequestLogger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/login")
public class ApiLoginServlet extends HttpServlet {

    private final LoginUseCase loginUseCase = new LoginUseCase();
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

        // 🆕 ADD: Log request for concurrency tracking
        String requestId = RequestLogger.logRequest("LOGIN", null, req.getRemoteAddr(), Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        try {
            // Parse JSON request with GSON
            LoginRequest loginRequest = gson.fromJson(req.getReader(), LoginRequest.class);

            // Execute use case (same as before)
            ApiResponse<UserDTO> response = loginUseCase.execute(loginRequest);

            if (response.isSuccess()) {
                UserDTO userData = response.getData();

                // 🆕 ADD: Create session with SessionManager
                String sessionId = SessionManager.createSession(
                        loginRequest.getUsername(),
                        String.valueOf(userData.getRoleId()),
                        req.getRemoteAddr(),
                        req.getHeader("User-Agent")
                );

                // Store in HTTP session (same as before)
                HttpSession session = req.getSession(true);
                session.setAttribute("sessionId", sessionId);
                session.setAttribute("username", loginRequest.getUsername());
                session.setAttribute("roleId", userData.getRoleId());
                session.setAttribute("fullName", userData.getFullName());

                // Build JSON response
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("sessionId", sessionId);
                responseData.put("username", loginRequest.getUsername());
                responseData.put("roleId", userData.getRoleId());
                responseData.put("fullName", userData.getFullName());
                responseData.put("email", userData.getEmail());

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(responseData));

                // 🆕 ADD: Log success
                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

            } else {
                int statusCode = response.getMessage().contains("required")
                        ? HttpServletResponse.SC_BAD_REQUEST
                        : HttpServletResponse.SC_UNAUTHORIZED;

                resp.setStatus(statusCode);
                resp.getWriter().write(gson.toJson(Map.of(
                        "success", false,
                        "message", response.getMessage()
                )));

                // 🆕 ADD: Log failure
                RequestLogger.updateStatus(requestId, "FAILED", startTime);
            }

        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            )));

            // 🆕 ADD: Log error
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