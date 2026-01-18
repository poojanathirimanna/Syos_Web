package com.syos.web.presentation.api.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.syos.web.concurrency.SessionManager;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.dao.UserDao;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ApiGoogleLoginServlet extends HttpServlet {

    private final UserDao dao = new UserDao();
    private final Gson gson = new Gson();
    private static final int DEFAULT_ROLE_ID = 3; // Customer role
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

        // Log request
        String requestId = RequestLogger.logRequest("GOOGLE_LOGIN", null, req.getRemoteAddr(), Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        try {
            // Parse JSON request with GSON
            Map<String, String> requestData = gson.fromJson(req.getReader(), Map.class);
            String credential = requestData.get("credential");

            if (credential == null || credential.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", false,
                        "message", "Missing credential."
                )));
                return;
            }

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
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", false,
                        "message", "Invalid Google token."
                )));
                RequestLogger.updateStatus(requestId, "FAILED", startTime);
                return;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            boolean emailVerified = payload.getEmailVerified();

            if (!emailVerified) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", false,
                        "message", "Email not verified by Google."
                )));
                RequestLogger.updateStatus(requestId, "FAILED", startTime);
                return;
            }

            // Check if user exists by Google ID
            String existingUserId = dao.findUserByGoogleId(googleId);

            if (existingUserId != null) {
                // User exists, log them in
                UserDao.UserDetails userDetails = dao.getUserDetails(existingUserId);

                // Create session with SessionManager
                String sessionId = SessionManager.createSession(
                        existingUserId,
                        String.valueOf(userDetails.getRoleId()),
                        req.getRemoteAddr(),
                        req.getHeader("User-Agent")
                );

                HttpSession session = req.getSession();
                session.setAttribute("sessionId", sessionId);
                session.setAttribute("username", existingUserId);
                session.setAttribute("roleId", userDetails.getRoleId());
                session.setAttribute("fullName", userDetails.getFullName());
                session.setMaxInactiveInterval(30 * 60);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", true,
                        "message", "Login successful.",
                        "sessionId", sessionId,
                        "userId", existingUserId,
                        "roleId", userDetails.getRoleId(),
                        "fullName", userDetails.getFullName() != null ? userDetails.getFullName() : "",
                        "email", userDetails.getEmail() != null ? userDetails.getEmail() : ""
                )));

                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
                return;
            }

            // Check if user exists by email
            if (dao.existsByEmail(email)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", false,
                        "message", "Email already registered. Please login with your password or link your Google account."
                )));
                RequestLogger.updateStatus(requestId, "FAILED", startTime);
                return;
            }

            // New user - create account
            String newUserId = generateUserId(email);
            boolean created = dao.registerGoogleUser(newUserId, name, email, googleId, DEFAULT_ROLE_ID);

            if (created) {
                // Create session for new user
                String sessionId = SessionManager.createSession(
                        newUserId,
                        String.valueOf(DEFAULT_ROLE_ID),
                        req.getRemoteAddr(),
                        req.getHeader("User-Agent")
                );

                HttpSession session = req.getSession();
                session.setAttribute("sessionId", sessionId);
                session.setAttribute("username", newUserId);
                session.setAttribute("roleId", DEFAULT_ROLE_ID);
                session.setAttribute("fullName", name);
                session.setMaxInactiveInterval(30 * 60);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", true,
                        "message", "Account created and logged in.",
                        "sessionId", sessionId,
                        "userId", newUserId,
                        "roleId", DEFAULT_ROLE_ID,
                        "fullName", name
                )));

                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of(
                        "ok", false,
                        "message", "Failed to create account."
                )));
                RequestLogger.updateStatus(requestId, "FAILED", startTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                    "ok", false,
                    "message", "Server error: " + e.getMessage()
            )));
            RequestLogger.updateStatus(requestId, "FAILED", startTime);
        }
    }

    private String generateUserId(String email) {
        if (email == null || email.isBlank()) {
            return "google_" + UUID.randomUUID().toString().substring(0, 8);
        }

        String baseId = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

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
}