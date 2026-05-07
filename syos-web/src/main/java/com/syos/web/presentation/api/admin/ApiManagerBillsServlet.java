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
import java.util.List;

/**
 * Manager Bills Servlet - For viewing ALL bills
 * GET /api/admin/bills        → Get all bills
 * GET /api/admin/bills/{id}   → Get bill details
 */
@WebServlet("/api/admin/bills/*")
public class ApiManagerBillsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private BillDao billDao;

    @Override
    public void init() {
        billDao = new BillDao();
        System.out.println("✅ ApiManagerBillsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check authentication and admin role
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized: Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        Integer roleId = (Integer) session.getAttribute("roleId");
        if (roleId == null || (roleId != 1 && roleId != 2)) {  // 1=ADMIN, 2=MANAGER/CASHIER
            ApiResponse<Object> error = ApiResponse.error("Forbidden: Admin/Manager access required");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all bills
                List<BillDTO> bills = billDao.getAllBills();

                ApiResponse<List<BillDTO>> response = ApiResponse.success(
                        "All bills retrieved successfully",
                        bills
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Get single bill by bill_number - ADMIN CAN ACCESS ANY BILL
                String billNumber = pathInfo.substring(1);
                System.out.println("🔍 Admin requesting bill details: " + billNumber);

                BillDTO bill = billDao.getBillByNumber(billNumber);

                if (bill == null) {
                    ApiResponse<Object> error = ApiResponse.error("Bill not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }

                System.out.println("✅ Admin: Bill found and returned: " + billNumber);

                ApiResponse<BillDTO> response = ApiResponse.success(
                        "Bill retrieved successfully",
                        bill
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            }

        } catch (NumberFormatException e) {
            ApiResponse<Object> error = ApiResponse.error("Invalid bill number format");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve bills: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error in ApiManagerBillsServlet.doGet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}