package com.syos.web.presentation.api.auth;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.RegisterRequest;
import com.syos.web.application.usecases.RegisterUseCase;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

public class ApiRegisterServlet extends HttpServlet {

    private final RegisterUseCase registerUseCase = new RegisterUseCase();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setContentType("application/json; charset=UTF-8");

        // Parse request
        String body = readBody(req);
        String userId = extractJsonValue(body, "user_id");
        String fullName = extractJsonValue(body, "full_name");
        String contactNumber = extractJsonValue(body, "contact_number");
        String email = extractJsonValue(body, "email");
        String password = extractJsonValue(body, "password");

        // Create DTO
        RegisterRequest registerRequest = new RegisterRequest(userId, fullName, email, contactNumber, password);

        // Execute use case
        ApiResponse response = registerUseCase.execute(registerRequest);

        // Handle response
        if (response.isOk()) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"ok\":true,\"message\":\"" + escape(response.getMessage()) + "\"}");
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
            resp.getWriter().write("{\"ok\":false,\"message\":\"" + escape(response.getMessage()) + "\"}");
        }
    }

    private static void addCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private static String extractJsonValue(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"";
        int i = json.indexOf(pattern);
        if (i < 0) return null;
        int colon = json.indexOf(":", i);
        if (colon < 0) return null;
        int firstQuote = json.indexOf("\"", colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote < 0) return null;
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

