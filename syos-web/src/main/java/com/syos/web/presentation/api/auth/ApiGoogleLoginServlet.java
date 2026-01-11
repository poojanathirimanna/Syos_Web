package com.syos.web.presentation.api.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.syos.web.dao.UserDao;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class ApiGoogleLoginServlet extends HttpServlet {

    private final UserDao dao = new UserDao();
    private static final int DEFAULT_ROLE_ID = 2; // Same as regular registration

    // Replace this with your actual Google Client ID from Google Cloud Console
    private static final String GOOGLE_CLIENT_ID = "997091192220-ulfhf1i9i9uc7qikfupkbgb4u67pjk28.apps.googleusercontent.com";

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        addCors(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCors(resp);
        resp.setContentType("application/json; charset=UTF-8");

        String body = readBody(req);
        String credential = extractJsonValue(body, "credential");

        if (credential == null || credential.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"ok\":false,\"message\":\"Missing credential.\"}");
            return;
        }

        try {
            // Verify the Google ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);

            if (idToken == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"ok\":false,\"message\":\"Invalid Google token.\"}");
                return;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            boolean emailVerified = payload.getEmailVerified();

            if (!emailVerified) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"ok\":false,\"message\":\"Email not verified by Google.\"}");
                return;
            }

            // Check if user exists by Google ID
            String existingUserId = dao.findUserByGoogleId(googleId);

            if (existingUserId != null) {
                // User exists, log them in
                HttpSession session = req.getSession();
                session.setAttribute("username", existingUserId);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"ok\":true,\"message\":\"Login successful.\",\"userId\":\"" + existingUserId + "\"}");
                return;
            }

            // Check if user exists by email (they might have registered with email/password)
            if (dao.existsByEmail(email)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"ok\":false,\"message\":\"Email already registered. Please login with your password or link your Google account.\"}");
                return;
            }

            // New user - create account
            String newUserId = generateUserId(email);
            boolean created = dao.registerGoogleUser(newUserId, name, email, googleId, DEFAULT_ROLE_ID);

            if (created) {
                // Auto-login the new user
                HttpSession session = req.getSession();
                session.setAttribute("username", newUserId);
                session.setMaxInactiveInterval(30 * 60);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"ok\":true,\"message\":\"Account created and logged in.\",\"userId\":\"" + newUserId + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"ok\":false,\"message\":\"Failed to create account.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"ok\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Generate a unique user ID from email
     */
    private String generateUserId(String email) {
        if (email == null || email.isBlank()) {
            return "google_" + UUID.randomUUID().toString().substring(0, 8);
        }

        String baseId = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // If userId already exists, append a number
        String userId = baseId;
        int counter = 1;
        while (dao.existsByUserId(userId)) {
            userId = baseId + counter;
            counter++;
        }

        return userId;
    }

    private static void addCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Vary", "Origin");
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
}

