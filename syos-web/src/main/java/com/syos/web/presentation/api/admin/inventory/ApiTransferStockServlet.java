package com.syos.web.presentation.api.admin.inventory;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.TransferStockRequest;
import com.syos.web.application.dto.TransferStockResponse;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.application.usecases.TransferStockUseCase;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.infrastructure.repositories.ProductRepositoryImpl;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Transfer Stock Servlet - Move stock between locations using FEFO
 * POST /api/admin/inventory/transfer
 */
@WebServlet("/api/admin/inventory/transfer")
public class ApiTransferStockServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private TransferStockUseCase transferStockUseCase;

    @Override
    public void init() {
        IProductRepository productRepository = new ProductRepositoryImpl();
        this.transferStockUseCase = new TransferStockUseCase(productRepository);

        System.out.println("✅ ApiTransferStockServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String requestId = RequestLogger.logRequest(
                "TRANSFER_STOCK",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            TransferStockRequest request = gson.fromJson(requestBody, TransferStockRequest.class);

            // Set user ID from session
            request.setUserId(userId);

            // Execute use case
            TransferStockResponse response = transferStockUseCase.execute(request);

            // Success response
            ApiResponse<TransferStockResponse> apiResponse = ApiResponse.success(
                    response.getMessage(),
                    response
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(apiResponse));

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to transfer stock: " + e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to transfer stock: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("Error in ApiTransferStockServlet: " + e.getMessage());
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