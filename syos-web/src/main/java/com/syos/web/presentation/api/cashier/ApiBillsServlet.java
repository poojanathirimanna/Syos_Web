package com.syos.web.presentation.api.cashier;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.application.usecases.CreateBillUseCase;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.concurrency.BillQueueService;
import com.syos.web.concurrency.BillQueueResponse;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

/**
 * Bills Servlet - For Cashiers
 * POST /api/cashier/bills        → Create bill (WITH QUEUE!)
 * GET  /api/cashier/bills        → Get cashier's own bills
 * GET  /api/cashier/bills/{id}   → Get bill details
 */
@WebServlet("/api/cashier/bills/*")
public class ApiBillsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private CreateBillUseCase createBillUseCase;
    private BillDao billDao;

    @Override
    public void init() {
        billDao = new BillDao();
        ProductDao productDao = new ProductDao();
        this.createBillUseCase = new CreateBillUseCase(billDao, productDao);

        BillQueueService.getInstance();  // Forces initialization

        System.out.println("✅ ApiBillsServlet (Cashier) initialized");
    }

    // ===================================================================
    // POST - Create bill (WITH QUEUE!)
    // ===================================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : null;

        if (userId == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized: Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String requestId = RequestLogger.logRequest(
                "CREATE_BILL",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            CreateBillRequest request = gson.fromJson(requestBody, CreateBillRequest.class);

            System.out.println("📨 [" + Thread.currentThread().getName() + "] Submitting bill request to queue...");

            // 🆕 SUBMIT TO QUEUE (instead of direct execution!)
            CompletableFuture<BillQueueResponse> future =
                    BillQueueService.getInstance().submitBillRequest(request, userId);

            // Wait for result with timeout (30 seconds)
            BillQueueResponse queueResponse = future.get(30, TimeUnit.SECONDS);

            if (queueResponse.isSuccess()) {
                // Success response
                ApiResponse<BillDTO> response = ApiResponse.success(
                        queueResponse.getMessage(),
                        queueResponse.getBillDTO()
                );

                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);

                System.out.println("✅ [" + Thread.currentThread().getName() + "] Bill created successfully in " +
                        queueResponse.getProcessingTimeMs() + "ms");

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(response));
            } else {
                // Queue processing failed
                RequestLogger.logError(requestId, queueResponse.getErrorMessage());
                ApiResponse<Object> errorResponse = ApiResponse.error(queueResponse.getErrorMessage());
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(errorResponse));
            }

        } catch (TimeoutException e) {
            // Request took too long (> 30 seconds)
            RequestLogger.logError(requestId, "Request timeout: " + e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Request processing timeout. Server is busy, please try again.");
            resp.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("⏱️ [" + Thread.currentThread().getName() + "] Request timeout: " + requestId);

        } catch (ExecutionException e) {
            // Processing error in worker thread
            RequestLogger.logError(requestId, "Processing error: " + e.getCause().getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to create bill: " + e.getCause().getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("❌ [" + Thread.currentThread().getName() + "] Processing error: " + e.getCause().getMessage());
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to create bill: " + e.getMessage());
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to create bill: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            System.err.println("Error in ApiBillsServlet.doPost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===================================================================
    // GET - List bills or get single bill
    // ===================================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : null;

        if (userId == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized: Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Get cashier's own bills
                List<BillDTO> bills = billDao.getBillsByCashier(userId);

                ApiResponse<List<BillDTO>> response = ApiResponse.success(
                        "Bills retrieved successfully",
                        bills
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Get single bill by bill_number
                String billNumber = pathInfo.substring(1);
                BillDTO bill = billDao.getBillByNumber(billNumber);

                if (bill == null) {
                    ApiResponse<Object> error = ApiResponse.error("Bill not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }

                // Check if bill belongs to this cashier
                if (!bill.getUserId().equals(userId)) {
                    ApiResponse<Object> error = ApiResponse.error("Unauthorized: This bill does not belong to you");
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }

                ApiResponse<BillDTO> response = ApiResponse.success(
                        "Bill retrieved successfully",
                        bill
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve bills: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiBillsServlet.doGet: " + e.getMessage());
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