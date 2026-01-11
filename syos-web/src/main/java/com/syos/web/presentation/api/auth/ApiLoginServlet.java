package com.syos.web.presentation.api.auth;

import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.LoginRequest;
import com.syos.web.application.usecases.LoginUseCase;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

public class ApiLoginServlet extends HttpServlet {

    private final LoginUseCase loginUseCase = new LoginUseCase();

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
        String username = extractJsonValue(body, "username");
        String password = extractJsonValue(body, "password");

        // Create DTO
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Execute use case
        ApiResponse response = loginUseCase.execute(loginRequest);

        // Handle response
        if (response.isOk()) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"ok\":true,\"username\":\"" + escape(username) + "\"}");
        } else {
            int statusCode = response.getMessage().contains("required")
                    ? HttpServletResponse.SC_BAD_REQUEST
                    : HttpServletResponse.SC_UNAUTHORIZED;
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
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

