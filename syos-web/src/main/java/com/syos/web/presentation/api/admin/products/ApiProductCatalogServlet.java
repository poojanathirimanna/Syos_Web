package com.syos.web.presentation.api.admin.products;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.ProductCatalogDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.application.usecases.ProductCatalogUseCase;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.infrastructure.repositories.ProductRepositoryImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet for viewing product catalog
 * GET /api/admin/products/catalog
 *
 * Clean Architecture Flow:
 * Servlet → Use Case → Repository → DAO → Database
 */
@WebServlet("/api/admin/products/catalog")
public class ApiProductCatalogServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private ProductCatalogUseCase productCatalogUseCase;

    @Override
    public void init() throws ServletException {
        // Initialize dependencies (Dependency Injection)
        IProductRepository productRepository = new ProductRepositoryImpl();
        this.productCatalogUseCase = new ProductCatalogUseCase(productRepository);

        System.out.println("✅ ApiProductCatalogServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Track start time for performance monitoring
        long startTime = System.currentTimeMillis();

        // Set response type
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Get session and user info
        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        // Log request with thread tracking
        String requestId = RequestLogger.logRequest(
                "VIEW_PRODUCT_CATALOG",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Execute use case
            ProductCatalogDTO catalogDTO = productCatalogUseCase.execute();

            // Create success response using your existing ApiResponse.success()
            ApiResponse response = ApiResponse.success(
                    "Product catalog retrieved successfully",
                    catalogDTO
            );

            // Log success using your existing updateStatus()
            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

            // Send response
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            // Log error using your existing logError()
            RequestLogger.logError(requestId, "Failed to retrieve product catalog: " + e.getMessage());

            // Create error response using your existing ApiResponse.error()
            ApiResponse errorResponse = ApiResponse.error(
                    "Failed to retrieve product catalog: " + e.getMessage()
            );

            // Send error response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));

            // Log exception
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle CORS preflight
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}