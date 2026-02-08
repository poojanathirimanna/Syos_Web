package com.syos.web.presentation.api.customer;

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
 * 📦 Customer Orders Servlet
 * GET    /api/customer/orders                → List all customer orders
 * GET    /api/customer/orders/{billNumber}  → Get single order details
 * PUT    /api/customer/orders/{billNumber}/cancel → Cancel order
 */
@WebServlet("/api/customer/orders/*")
public class ApiCustomerOrdersServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private BillDao billDao;

    @Override
    public void init() {
        billDao = new BillDao();
        System.out.println("✅ ApiCustomerOrdersServlet initialized");
    }

    /**
     * GET - List all orders or get single order
     */
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

        String customerId = (String) session.getAttribute("username");
        String pathInfo = req.getPathInfo();

        System.out.println("🔍 DEBUG ApiCustomerOrdersServlet.doGet:");
        System.out.println("   Session username: '" + customerId + "'");
        System.out.println("   Session ID: " + session.getId());
        System.out.println("   Path info: " + (pathInfo == null ? "NULL (listing all orders)" : pathInfo));

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all orders for this customer
                System.out.println("   📋 Fetching all orders for customer: " + customerId);
                List<BillDTO> orders = billDao.getOrdersByCustomer(customerId);

                System.out.println("   📊 DAO returned " + orders.size() + " orders");

                ApiResponse<List<BillDTO>> response = ApiResponse.success(
                        orders.isEmpty() ? "No orders found" : "Orders retrieved successfully",
                        orders
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

                System.out.println("📦 Retrieved " + orders.size() + " orders for customer: " + customerId);

            } else {
                // Get single order by bill number
                String billNumber = pathInfo.substring(1); // Remove leading "/"

                BillDTO order = billDao.getOrderByCustomer(billNumber, customerId);

                if (order == null) {
                    ApiResponse<Object> error = ApiResponse.error("Order not found or does not belong to you");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }

                ApiResponse<BillDTO> response = ApiResponse.success(
                        "Order details retrieved successfully",
                        order
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

                System.out.println("📦 Retrieved order details: " + billNumber + " for customer: " + customerId);
            }

        } catch (SQLException e) {
            ApiResponse<Object> error = ApiResponse.error("Database error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error retrieving orders: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve orders: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error in ApiCustomerOrdersServlet.doGet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * PUT - Cancel order
     * Path should end with /cancel: /api/customer/orders/{billNumber}/cancel
     */
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

        String customerId = (String) session.getAttribute("username");
        String pathInfo = req.getPathInfo();

        // Validate path format: /{billNumber}/cancel
        if (pathInfo == null || !pathInfo.contains("/cancel")) {
            ApiResponse<Object> error = ApiResponse.error("Invalid path. Use: /api/customer/orders/{billNumber}/cancel");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        // Extract bill number from path
        String billNumber = pathInfo.substring(1, pathInfo.indexOf("/cancel"));

        if (billNumber.trim().isEmpty()) {
            ApiResponse<Object> error = ApiResponse.error("Bill number is required");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            // First verify the order belongs to this customer and get its current status
            BillDTO order = billDao.getOrderByCustomer(billNumber, customerId);

            if (order == null) {
                ApiResponse<Object> error = ApiResponse.error("Order not found or does not belong to you");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Check if order can be cancelled
            String currentStatus = order.getOrderStatus();
            if (currentStatus == null) {
                ApiResponse<Object> error = ApiResponse.error("This is not an online order");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if ("CANCELLED".equals(currentStatus)) {
                ApiResponse<Object> error = ApiResponse.error("Order is already cancelled");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if ("SHIPPED".equals(currentStatus) || "DELIVERED".equals(currentStatus)) {
                ApiResponse<Object> error = ApiResponse.error("Cannot cancel order - already " + currentStatus.toLowerCase());
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Cancel the order
            boolean cancelled = billDao.cancelOrder(billNumber, customerId);

            if (cancelled) {
                ApiResponse<String> response = ApiResponse.success(
                        "Order cancelled successfully",
                        "Your order " + billNumber + " has been cancelled"
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

                System.out.println("🚫 Order cancelled: " + billNumber + " by customer: " + customerId);

            } else {
                ApiResponse<Object> error = ApiResponse.error("Failed to cancel order. It may have already been processed.");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (SQLException e) {
            ApiResponse<Object> error = ApiResponse.error("Database error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error cancelling order: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to cancel order: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error in ApiCustomerOrdersServlet.doPut: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

