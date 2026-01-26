package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.CashierDTO;
import com.syos.web.application.dto.CreateCashierRequest;
import com.syos.web.application.dto.UpdateCashierRequest;
import com.syos.web.infrastructure.persistence.dao.UserDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cashiers Management Servlet
 * GET    /api/admin/cashiers       → List all cashiers
 * GET    /api/admin/cashiers/{id}  → Get single cashier
 * POST   /api/admin/cashiers       → Create cashier
 * PUT    /api/admin/cashiers       → Update cashier
 * DELETE /api/admin/cashiers/{id}  → Deactivate cashier
 */
@WebServlet("/api/admin/cashiers/*")
public class ApiCashiersServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final UserDao userDao = new UserDao();

    @Override
    public void init() {
        System.out.println("✅ ApiCashiersServlet initialized");
    }

    // ===================================================================
    // GET - List all cashiers OR get single cashier
    // ===================================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all cashiers
                List<UserDao.UserDetails> cashiers = userDao.getAllCashiers();
                List<CashierDTO> cashierDTOs = convertToDTOList(cashiers);

                ApiResponse<List<CashierDTO>> response = ApiResponse.success(
                        "Cashiers retrieved successfully",
                        cashierDTOs
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Get single cashier by ID
                String userId = pathInfo.substring(1); // Remove leading "/"
                UserDao.UserDetails cashier = userDao.getCashierById(userId);

                if (cashier == null) {
                    ApiResponse<Object> error = ApiResponse.error("Cashier not found: " + userId);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }

                CashierDTO cashierDTO = convertToDTO(cashier);
                ApiResponse<CashierDTO> response = ApiResponse.success(
                        "Cashier retrieved successfully",
                        cashierDTO
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve cashiers: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiCashiersServlet.doGet: " + e.getMessage());
        }
    }

    // ===================================================================
    // POST - Create new cashier
    // ===================================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            CreateCashierRequest request = gson.fromJson(requestBody, CreateCashierRequest.class);

            // Validate request
            request.validate();

            // Check if user ID already exists
            if (userDao.existsByUserId(request.getUserId())) {
                ApiResponse<Object> error = ApiResponse.error("User ID '" + request.getUserId() + "' already exists");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Check if email already exists
            if (userDao.existsByEmail(request.getEmail())) {
                ApiResponse<Object> error = ApiResponse.error("Email '" + request.getEmail() + "' already exists");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Create cashier
            boolean success = userDao.createCashier(
                    request.getUserId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getContactNumber(),
                    request.getPassword()
            );

            if (success) {
                CashierDTO cashierDTO = new CashierDTO(
                        request.getUserId(),
                        request.getFullName(),
                        request.getEmail(),
                        request.getContactNumber(),
                        true  // isActive = true by default
                );

                ApiResponse<CashierDTO> response = ApiResponse.success(
                        "Cashier created successfully",
                        cashierDTO
                );

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(response));
            } else {
                ApiResponse<Object> error = ApiResponse.error("Failed to create cashier");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to create cashier: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiCashiersServlet.doPost: " + e.getMessage());
        }
    }

    // ===================================================================
    // PUT - Update cashier
    // ===================================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            UpdateCashierRequest request = gson.fromJson(requestBody, UpdateCashierRequest.class);

            // Validate request
            request.validate();

            // Check if cashier exists
            UserDao.UserDetails existingCashier = userDao.getCashierById(request.getUserId());
            if (existingCashier == null) {
                ApiResponse<Object> error = ApiResponse.error("Cashier not found: " + request.getUserId());
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Update cashier
            boolean success = userDao.updateCashier(
                    request.getUserId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getContactNumber(),
                    request.getPassword()  // Can be null (won't update password)
            );

            if (success) {
                // Get updated cashier
                UserDao.UserDetails updatedCashier = userDao.getCashierById(request.getUserId());
                CashierDTO cashierDTO = convertToDTO(updatedCashier);

                ApiResponse<CashierDTO> response = ApiResponse.success(
                        "Cashier updated successfully",
                        cashierDTO
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            } else {
                ApiResponse<Object> error = ApiResponse.error("Failed to update cashier");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update cashier: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiCashiersServlet.doPut: " + e.getMessage());
        }
    }

    // ===================================================================
    // DELETE - Deactivate cashier
    // ===================================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                ApiResponse<Object> error = ApiResponse.error("Cashier ID is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            String userId = pathInfo.substring(1);

            // Check if cashier exists
            UserDao.UserDetails cashier = userDao.getCashierById(userId);
            if (cashier == null) {
                ApiResponse<Object> error = ApiResponse.error("Cashier not found: " + userId);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Deactivate cashier
            boolean success = userDao.deactivateCashier(userId);

            if (success) {
                ApiResponse<Object> response = ApiResponse.success(
                        "Cashier deactivated successfully",
                        null
                );

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            } else {
                ApiResponse<Object> error = ApiResponse.error("Failed to deactivate cashier");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(error));
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to deactivate cashier: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("Error in ApiCashiersServlet.doDelete: " + e.getMessage());
        }
    }

    // ===================================================================
    // Helper Methods
    // ===================================================================

    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private CashierDTO convertToDTO(UserDao.UserDetails userDetails) {
        return new CashierDTO(
                userDetails.getUserId(),
                userDetails.getFullName(),
                userDetails.getEmail(),
                userDetails.getContactNumber(),
                userDetails.isActive()
        );
    }

    private List<CashierDTO> convertToDTOList(List<UserDao.UserDetails> userDetailsList) {
        List<CashierDTO> dtoList = new ArrayList<>();
        for (UserDao.UserDetails userDetails : userDetailsList) {
            dtoList.add(convertToDTO(userDetails));
        }
        return dtoList;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}