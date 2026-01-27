package com.syos.web.presentation.api.cashier;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.application.usecases.CreateBillUseCase;
import com.syos.web.concurrency.RequestLogger;
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

/**
 * Bills Servlet - For Cashiers
 * POST /api/cashier/bills        → Create bill
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

        System.out.println("✅ ApiBillsServlet (Cashier) initialized");
    }

    // ===================================================================
    // POST - Create bill
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

            // Execute use case
            BillDTO billDTO = createBillUseCase.execute(request, userId);

            // Success response
            ApiResponse<BillDTO> response = ApiResponse.success(
                    "Bill created successfully",
                    billDTO
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
                // Get single bill by bill_number (not ID!)
                String billNumber = pathInfo.substring(1);  // ✅ Changed from Long to String
                BillDTO bill = billDao.getBillByNumber(billNumber);  // ✅ Correct method name

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