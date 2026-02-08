package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.BillDTO;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 📦 Admin Order Management Servlet
 * GET    /api/admin/orders                → List all customer orders (ADMIN view)
 * PUT    /api/admin/orders/{billNumber}/approve-payment  → Approve payment
 * PUT    /api/admin/orders/{billNumber}/status           → Update order status
 */
@WebServlet("/api/admin/orders/*")
public class ApiAdminOrdersServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private BillDao billDao;

    @Override
    public void init() {
        this.billDao = new BillDao();
        System.out.println("✅ ApiAdminOrdersServlet initialized");
    }

    // GET - List all customer orders (for admin dashboard)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        // TODO: Check if user is ADMIN (roleId = 1)

        try {
            List<BillDTO> orders = billDao.getAllCustomerOrders();

            ApiResponse<List<BillDTO>> response = ApiResponse.success(
                    orders.isEmpty() ? "No customer orders found" : "Orders retrieved successfully",
                    orders
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("📊 Admin retrieved " + orders.size() + " customer orders");

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve orders: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // PUT - Update order (approve payment or change status)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            ApiResponse<Object> error = ApiResponse.error("Invalid path");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            // Path: /BILL-xxx/approve-payment or /BILL-xxx/status
            if (pathInfo.contains("/approve-payment")) {
                handleApprovePayment(req, resp, pathInfo);
            } else if (pathInfo.contains("/status")) {
                handleUpdateStatus(req, resp, pathInfo);
            } else {
                ApiResponse<Object> error = ApiResponse.error("Unknown action");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update order: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    /**
     * Approve payment for an order
     */
    private void handleApprovePayment(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        // Extract bill number: /BILL-xxx/approve-payment
        String billNumber = pathInfo.split("/")[1];

        boolean updated = billDao.approvePayment(billNumber);

        if (updated) {
            ApiResponse<String> response = ApiResponse.success(
                    "Payment approved successfully",
                    "Order " + billNumber + " payment status updated to PAID"
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Payment approved for order: " + billNumber);

        } else {
            ApiResponse<Object> error = ApiResponse.error("Failed to approve payment - Order not found or already processed");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
        }
    }

    /**
     * Update order status
     */
    private void handleUpdateStatus(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException, SQLException {
        // Extract bill number: /BILL-xxx/status
        String billNumber = pathInfo.split("/")[1];

        // Read request body to get new status
        String requestBody = readRequestBody(req);
        UpdateStatusRequest request = gson.fromJson(requestBody, UpdateStatusRequest.class);

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            ApiResponse<Object> error = ApiResponse.error("Status is required");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        boolean updated = billDao.updateOrderStatus(billNumber, request.getStatus());

        if (updated) {
            ApiResponse<String> response = ApiResponse.success(
                    "Order status updated successfully",
                    "Order " + billNumber + " status changed to " + request.getStatus()
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Order status updated: " + billNumber + " → " + request.getStatus());

        } else {
            ApiResponse<Object> error = ApiResponse.error("Failed to update status - Order not found");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
        }
    }

    // Helper: Read request body
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    // DTO for status update request
    private static class UpdateStatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

