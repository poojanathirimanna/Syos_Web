package com.syos.web.presentation.api.auth;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.RegisterRequest;
import com.syos.web.application.usecases.RegisterUseCase;
import com.syos.web.concurrency.RequestLogger;  // 🆕 ADD

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/register")
public class ApiRegisterServlet extends HttpServlet {

    private final RegisterUseCase registerUseCase = new RegisterUseCase();
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

        // 🆕 ADD: Log request
        String requestId = RequestLogger.logRequest("REGISTER", null, req.getRemoteAddr(), Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        try {
            // Parse JSON request
            Map<String, String> requestData = gson.fromJson(req.getReader(), Map.class);

            // Create DTO
            RegisterRequest registerRequest = new RegisterRequest(
                    requestData.get("user_id"),
                    requestData.get("full_name"),
                    requestData.get("email"),
                    requestData.get("contact_number"),
                    requestData.get("password")
            );

            // Execute use case
            ApiResponse<Object> response = registerUseCase.execute(registerRequest);

            // Handle response
            if (response.isSuccess()) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(Map.of(
                        "success", true,
                        "message", response.getMessage()
                )));

                // 🆕 ADD: Log success
                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

            } else {
                int statusCode;
                if (response.getMessage().contains("required")) {
                    statusCode = HttpServletResponse.SC_BAD_REQUEST;
                } else if (response.getMessage().contains("exists")) {
                    statusCode = HttpServletResponse.SC_CONFLICT;
                } else {
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                }

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