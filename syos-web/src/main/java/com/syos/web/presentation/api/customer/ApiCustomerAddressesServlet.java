package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.AddressDTO;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.usecases.ManageAddressesUseCase;
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
 * Customer Addresses Servlet
 * Handles delivery address operations
 *
 * GET    /api/customer/addresses               → Get all addresses
 * POST   /api/customer/addresses               → Add address
 * PUT    /api/customer/addresses/{id}          → Update address
 * DELETE /api/customer/addresses/{id}          → Delete address
 * POST   /api/customer/addresses/{id}/default  → Set as default
 */
@WebServlet("/api/customer/addresses/*")
public class ApiCustomerAddressesServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final ManageAddressesUseCase manageAddressesUseCase = new ManageAddressesUseCase();

    @Override
    public void init() {
        System.out.println("✅ ApiCustomerAddressesServlet initialized");
    }

    // GET - Get all addresses
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("username");

        try {
            List<AddressDTO> addresses = manageAddressesUseCase.getUserAddresses(userId);

            ApiResponse<List<AddressDTO>> response = ApiResponse.success(
                    "Addresses retrieved successfully",
                    addresses
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to get addresses: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // POST - Add address or set default
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("username");

        try {
            String pathInfo = req.getPathInfo();

            // Check if setting default address
            if (pathInfo != null && pathInfo.contains("/default")) {
                // Extract address ID
                String[] parts = pathInfo.split("/");
                if (parts.length >= 2) {
                    Integer addressId = Integer.parseInt(parts[1]);

                    manageAddressesUseCase.setDefaultAddress(userId, addressId);

                    ApiResponse<Object> response = ApiResponse.success("Default address updated", null);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }
            }

            // Otherwise, add new address
            String requestBody = readRequestBody(req);
            AddressDTO request = gson.fromJson(requestBody, AddressDTO.class);

            AddressDTO address = manageAddressesUseCase.addAddress(userId, request);

            ApiResponse<AddressDTO> response = ApiResponse.success(
                    "Address added successfully",
                    address
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to process request: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // PUT - Update address
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("username");

        try {
            String pathInfo = req.getPathInfo();
            Integer addressId = extractAddressId(pathInfo);

            if (addressId == null) {
                throw new IllegalArgumentException("Address ID is required");
            }

            String requestBody = readRequestBody(req);
            AddressDTO request = gson.fromJson(requestBody, AddressDTO.class);

            manageAddressesUseCase.updateAddress(userId, addressId, request);

            ApiResponse<Object> response = ApiResponse.success("Address updated successfully", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update address: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // DELETE - Delete address
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("username");

        try {
            String pathInfo = req.getPathInfo();
            Integer addressId = extractAddressId(pathInfo);

            if (addressId == null) {
                throw new IllegalArgumentException("Address ID is required");
            }

            manageAddressesUseCase.deleteAddress(userId, addressId);

            ApiResponse<Object> response = ApiResponse.success("Address deleted successfully", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to delete address: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // Helper: Extract address ID from path
    private Integer extractAddressId(String pathInfo) {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                String idStr = pathInfo.substring(1).split("/")[0];
                return Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Helper: Read request body
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
