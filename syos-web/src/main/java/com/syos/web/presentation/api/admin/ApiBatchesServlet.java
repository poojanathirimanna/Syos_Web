package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.domain.model.StockBatch;
import com.syos.web.infrastructure.persistence.dao.StockBatchDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Batch Management Servlet - Get batches for a product
 * GET /api/admin/batches?productCode={code} → Get all batches for product
 */
@WebServlet("/api/admin/batches")
public class ApiBatchesServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final StockBatchDao batchDao = new StockBatchDao();

    @Override
    public void init() {
        System.out.println("✅ ApiBatchesServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            // Get productCode from query parameter
            String productCode = req.getParameter("productCode");

            if (productCode == null || productCode.trim().isEmpty()) {
                ApiResponse<Object> error = ApiResponse.error("Product code is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Get all batches for this product
            List<StockBatch> batches = batchDao.findByProductCode(productCode);

            ApiResponse<List<StockBatch>> response = ApiResponse.success(
                    "Batches retrieved successfully",
                    batches
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve batches: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
