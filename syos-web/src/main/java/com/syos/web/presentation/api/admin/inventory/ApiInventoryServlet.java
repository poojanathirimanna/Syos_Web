package com.syos.web.presentation.api.admin.inventory;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.InventorySummaryDTO;
import com.syos.web.application.ports.IInventoryLocationRepository;
import com.syos.web.application.usecases.GetInventoryUseCase;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.infrastructure.repositories.InventoryLocationRepositoryImpl;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Inventory Servlet - View inventory levels
 * GET /api/admin/inventory                  → All inventory
 * GET /api/admin/inventory?location=MAIN    → Filter by location
 * GET /api/admin/inventory?product=RICE001  → Filter by product
 * GET /api/admin/inventory?view=expired     → Expired batches
 * GET /api/admin/inventory?view=near-expiry → Near expiry batches
 */
@WebServlet("/api/admin/inventory")
public class ApiInventoryServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private GetInventoryUseCase getInventoryUseCase;

    @Override
    public void init() {
        IInventoryLocationRepository inventoryRepository = new InventoryLocationRepositoryImpl();
        this.getInventoryUseCase = new GetInventoryUseCase(inventoryRepository);

        System.out.println("✅ ApiInventoryServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        // Get query parameters
        String location = req.getParameter("location");
        String productCode = req.getParameter("product");
        String view = req.getParameter("view");

        String requestId = RequestLogger.logRequest(
                "VIEW_INVENTORY",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            InventorySummaryDTO inventorySummary;

            // Route based on parameters
            if (view != null) {
                if (view.equals("expired")) {
                    inventorySummary = getInventoryUseCase.executeExpired();
                } else if (view.equals("near-expiry")) {
                    inventorySummary = getInventoryUseCase.executeNearExpiry(30); // 30 days threshold
                } else {
                    throw new IllegalArgumentException("Invalid view parameter: " + view);
                }
            } else if (location != null) {
                inventorySummary = getInventoryUseCase.executeByLocation(location.toUpperCase());
            } else if (productCode != null) {
                inventorySummary = getInventoryUseCase.executeByProduct(productCode);
            } else {
                inventorySummary = getInventoryUseCase.execute();
            }

            ApiResponse<InventorySummaryDTO> response = ApiResponse.success(
                    "Inventory retrieved successfully",
                    inventorySummary
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to retrieve inventory: " + e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to retrieve inventory: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("Error in ApiInventoryServlet: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}