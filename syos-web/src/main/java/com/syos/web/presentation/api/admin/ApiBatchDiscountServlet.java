package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.SetBatchDiscountRequest;
import com.syos.web.infrastructure.persistence.dao.StockBatchDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Batch Discount Management - Manager Only
 * POST   /api/admin/batches/discount → Set batch discount
 * DELETE /api/admin/batches/discount/{batchId} → Remove batch discount
 */
@WebServlet("/api/admin/batches/discount/*")
public class ApiBatchDiscountServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final StockBatchDao batchDao = new StockBatchDao();

    @Override
    public void init() {
        System.out.println("✅ ApiBatchDiscountServlet initialized");
    }

    // Set or update batch discount
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            String requestBody = readRequestBody(req);
            SetBatchDiscountRequest request = gson.fromJson(requestBody, SetBatchDiscountRequest.class);
            request.validate();

            boolean success = batchDao.setBatchDiscount(
                    request.getBatchId(),
                    request.getDiscountPercentage(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            if (success) {
                ApiResponse<Object> response = ApiResponse.success(
                        "Batch discount set successfully",
                        null
                );
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            } else {
                ApiResponse<Object> error = ApiResponse.error("Batch not found");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to set discount: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // Remove batch discount
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                ApiResponse<Object> error = ApiResponse.error("Batch ID is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            Integer batchId = Integer.parseInt(pathInfo.substring(1));
            boolean success = batchDao.removeBatchDiscount(batchId);

            if (success) {
                ApiResponse<Object> response = ApiResponse.success(
                        "Batch discount removed successfully",
                        null
                );
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            } else {
                ApiResponse<Object> error = ApiResponse.error("Batch not found");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (NumberFormatException e) {
            ApiResponse<Object> error = ApiResponse.error("Invalid batch ID");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to remove discount: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

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