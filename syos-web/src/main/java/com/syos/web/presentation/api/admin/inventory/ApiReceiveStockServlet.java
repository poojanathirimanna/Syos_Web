package com.syos.web.presentation.api.admin.inventory;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.ReceiveStockRequest;
import com.syos.web.application.dto.StockBatchDTO;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.application.ports.IStockBatchRepository;
import com.syos.web.application.usecases.ReceiveStockUseCase;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.infrastructure.repositories.ProductRepositoryImpl;
import com.syos.web.infrastructure.repositories.StockBatchRepositoryImpl;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Receive Stock Servlet - Add stock from supplier
 * POST /api/admin/inventory/receive
 */
@WebServlet("/api/admin/inventory/receive")
public class ApiReceiveStockServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private ReceiveStockUseCase receiveStockUseCase;

    @Override
    public void init() {
        IStockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
        IProductRepository productRepository = new ProductRepositoryImpl();
        this.receiveStockUseCase = new ReceiveStockUseCase(stockBatchRepository, productRepository);

        System.out.println("✅ ApiReceiveStockServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String requestId = RequestLogger.logRequest(
                "RECEIVE_STOCK",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            ReceiveStockRequest request = gson.fromJson(requestBody, ReceiveStockRequest.class);

            // Execute use case
            StockBatchDTO stockBatchDTO = receiveStockUseCase.execute(request);

            // Success response
            ApiResponse<StockBatchDTO> response = ApiResponse.success(
                    "Stock received successfully",
                    stockBatchDTO
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to receive stock: " + e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to receive stock: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("Error in ApiReceiveStockServlet: " + e.getMessage());
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