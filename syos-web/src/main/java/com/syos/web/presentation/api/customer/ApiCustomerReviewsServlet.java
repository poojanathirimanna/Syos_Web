package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.AddReviewRequest;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.ReviewDTO;
import com.syos.web.application.usecases.*;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Customer Reviews Servlet
 * Handles product review operations
 *
 * GET    /api/customer/reviews           → Get user's reviews
 * POST   /api/customer/reviews           → Add review
 * PUT    /api/customer/reviews/{id}      → Update review
 * DELETE /api/customer/reviews/{id}      → Delete review
 */
@WebServlet("/api/customer/reviews/*")
public class ApiCustomerReviewsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final AddReviewUseCase addReviewUseCase = new AddReviewUseCase();
    private final UpdateReviewUseCase updateReviewUseCase = new UpdateReviewUseCase();
    private final DeleteReviewUseCase deleteReviewUseCase = new DeleteReviewUseCase();
    private final GetUserReviewsUseCase getUserReviewsUseCase = new GetUserReviewsUseCase();

    @Override
    public void init() {
        System.out.println("✅ ApiCustomerReviewsServlet initialized");
    }

    // GET - Get user's reviews
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            List<ReviewDTO> reviews = getUserReviewsUseCase.execute(userId);

            ApiResponse<List<ReviewDTO>> response = ApiResponse.success(
                    "Reviews retrieved successfully",
                    reviews
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to get reviews: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // POST - Add review
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String requestBody = readRequestBody(req);
            AddReviewRequest request = gson.fromJson(requestBody, AddReviewRequest.class);

            ReviewDTO review = addReviewUseCase.execute(userId, request);

            ApiResponse<ReviewDTO> response = ApiResponse.success(
                    "Review submitted successfully",
                    review
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to add review: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // PUT - Update review
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String pathInfo = req.getPathInfo();
            Integer reviewId = extractReviewId(pathInfo);

            if (reviewId == null) {
                throw new IllegalArgumentException("Review ID is required");
            }

            String requestBody = readRequestBody(req);
            AddReviewRequest request = gson.fromJson(requestBody, AddReviewRequest.class);

            updateReviewUseCase.execute(reviewId, userId, request);

            ApiResponse<Object> response = ApiResponse.success("Review updated successfully", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update review: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // DELETE - Delete review
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String pathInfo = req.getPathInfo();
            Integer reviewId = extractReviewId(pathInfo);

            if (reviewId == null) {
                throw new IllegalArgumentException("Review ID is required");
            }

            deleteReviewUseCase.execute(reviewId, userId);

            ApiResponse<Object> response = ApiResponse.success("Review deleted successfully", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to delete review: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // Helper: Extract review ID from path
    private Integer extractReviewId(String pathInfo) {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                return Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Helper: Read request body
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}