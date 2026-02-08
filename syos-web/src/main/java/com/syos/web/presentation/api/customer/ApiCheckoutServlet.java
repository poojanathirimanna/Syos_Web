package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.concurrency.BillQueueResponse;
import com.syos.web.concurrency.BillQueueService;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 🛒 Customer Checkout Servlet
 * POST /api/customer/checkout → Process customer order (ONLINE checkout)
 *
 * 🔥 USES THE SAME BILLQUEUE AS CASHIERS! 🔥
 * Both cashiers and customers share the same queue and workers for fair processing
 */
@WebServlet("/api/customer/checkout")
public class ApiCheckoutServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();

    @Override
    public void init() {
        // Ensure BillQueueService is initialized
        BillQueueService.getInstance();
        System.out.println("✅ ApiCheckoutServlet (Customer) initialized - Using shared BillQueue");
    }

    /**
     * POST - Process customer checkout (create online order)
     * Request body example:
     * {
     *   "items": [
     *     {"productCode": "P001", "quantity": 2},
     *     {"productCode": "P002", "quantity": 1}
     *   ],
     *   "paymentMethod": "CREDIT_CARD",
     *   "channel": "ONLINE",
     *   "deliveryAddressId": 1,
     *   "deliveryAddress": "123 Main St",
     *   "deliveryCity": "Colombo",
     *   "deliveryPostalCode": "10100",
     *   "deliveryPhone": "0771234567",
     *   "paymentMethodDetails": "{\"cardNumber\":\"****1234\",\"cardType\":\"Visa\"}"
     * }
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.currentTimeMillis();
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

        String userId = (String) session.getAttribute("username");
        String requestId = RequestLogger.logRequest(
                "CUSTOMER_CHECKOUT",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Parse request
            String requestBody = readRequestBody(req);
            CreateBillRequest request = gson.fromJson(requestBody, CreateBillRequest.class);

            System.out.println("🔍 DEBUG ApiCheckoutServlet - BEFORE setting channel:");
            System.out.println("   Channel from request: '" + request.getChannel() + "'");

            // ✅ FORCE channel to ONLINE for customer checkout (always override)
            request.setChannel("ONLINE");

            System.out.println("🔍 DEBUG ApiCheckoutServlet - AFTER setting channel:");
            System.out.println("   Channel value: '" + request.getChannel() + "'");
            System.out.println("   Payment method: '" + request.getPaymentMethod() + "'");

            // Validate request
            request.validate();

            System.out.println("🛒 [" + Thread.currentThread().getName() +
                    "] Customer checkout request from: " + userId);

            // 🔥 SUBMIT TO SHARED QUEUE (same as cashiers!)
            CompletableFuture<BillQueueResponse> future =
                    BillQueueService.getInstance().submitBillRequest(request, userId, "CUSTOMER");

            // Wait for result with timeout (30 seconds)
            BillQueueResponse queueResponse = future.get(30, TimeUnit.SECONDS);

            if (queueResponse.isSuccess()) {
                // Success - Order created
                BillDTO order = queueResponse.getBillDTO();

                ApiResponse<BillDTO> response = ApiResponse.success(
                        "Order placed successfully! 🎉",
                        order
                );

                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

                System.out.println("✅ [" + Thread.currentThread().getName() +
                        "] Customer order created: " + order.getBillNumber() +
                        " (processed in " + queueResponse.getProcessingTimeMs() + "ms)");

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Queue processing failed
                RequestLogger.logError(requestId, queueResponse.getErrorMessage());

                ApiResponse<Object> errorResponse = ApiResponse.error(
                        "Failed to process checkout: " + queueResponse.getErrorMessage()
                );

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(errorResponse));
            }

        } catch (TimeoutException e) {
            // Request took too long (> 30 seconds)
            RequestLogger.logError(requestId, "Checkout timeout: " + e.getMessage());

            ApiResponse<Object> errorResponse = ApiResponse.error(
                    "⏱️ Checkout is taking longer than expected. " +
                    "Your order may still be processing. Please check your orders page."
            );

            resp.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            resp.getWriter().write(gson.toJson(errorResponse));

            System.err.println("⏱️ [" + Thread.currentThread().getName() +
                    "] Customer checkout timeout: " + requestId);

        } catch (ExecutionException e) {
            // Processing error in worker thread
            RequestLogger.logError(requestId, "Processing error: " + e.getCause().getMessage());

            String errorMessage = e.getCause() != null ?
                    e.getCause().getMessage() : e.getMessage();

            ApiResponse<Object> errorResponse = ApiResponse.error(
                    "Failed to process checkout: " + errorMessage
            );

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));

            System.err.println("❌ [" + Thread.currentThread().getName() +
                    "] Customer checkout error: " + errorMessage);
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            // Validation error
            RequestLogger.logError(requestId, e.getMessage());

            ApiResponse<Object> errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (InterruptedException e) {
            // Thread interrupted
            Thread.currentThread().interrupt();
            RequestLogger.logError(requestId, "Request interrupted");

            ApiResponse<Object> errorResponse = ApiResponse.error(
                    "Request was interrupted. Please try again."
            );

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            // Unexpected error
            RequestLogger.logError(requestId, "Checkout failed: " + e.getMessage());

            ApiResponse<Object> errorResponse = ApiResponse.error(
                    "Failed to process checkout: " + e.getMessage()
            );

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));

            System.err.println("❌ Error in ApiCheckoutServlet.doPost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to read request body
     */
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}

