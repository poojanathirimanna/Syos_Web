package com.syos.web.presentation.api.cashier;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.PromotionDTO;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.StockBatchDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Promotions/Discounts Servlet - For Cashiers
 * GET /api/cashier/promotions → Get all active promotions
 */
@WebServlet("/api/cashier/promotions")
public class ApiPromotionsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final ProductDao productDao = new ProductDao();
    private final StockBatchDao batchDao = new StockBatchDao();

    @Override
    public void init() {
        System.out.println("✅ ApiPromotionsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Get all active product discounts
            List<PromotionDTO> productPromotions = productDao.getAllActiveProductDiscounts();

            // Get all active batch discounts
            List<PromotionDTO> batchPromotions = batchDao.getAllActiveBatchDiscounts();

            // Combine both lists
            productPromotions.addAll(batchPromotions);

            ApiResponse<List<PromotionDTO>> response = ApiResponse.success(
                    "Active promotions retrieved successfully",
                    productPromotions
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve promotions: " + e.getMessage());
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