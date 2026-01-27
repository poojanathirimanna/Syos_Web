package com.syos.web.presentation.api.cashier;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.AvailableProductDTO;
import com.syos.web.infrastructure.persistence.dao.AvailableProductsDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Available Products Servlet - For Cashiers
 * GET /api/cashier/available-products
 */
@WebServlet("/api/cashier/available-products")
public class ApiAvailableProductsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final AvailableProductsDao availableProductsDao = new AvailableProductsDao();

    @Override
    public void init() {
        System.out.println("✅ ApiAvailableProductsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<AvailableProductDTO> products = availableProductsDao.getAvailableProducts();

            ApiResponse<List<AvailableProductDTO>> response = ApiResponse.success(
                    "Available products retrieved successfully",
                    products
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve products: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiAvailableProductsServlet: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}





