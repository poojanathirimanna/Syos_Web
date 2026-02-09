package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.WishlistItemDTO;
import com.syos.web.application.usecases.ManageWishlistUseCase;
import com.syos.web.domain.model.Wishlist;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Wishlist Servlet
 * Handles wishlist operations
 *
 * GET    /api/customer/wishlist                → Get wishlist
 * POST   /api/customer/wishlist                → Add to wishlist
 * DELETE /api/customer/wishlist/{productCode}  → Remove from wishlist
 */
@WebServlet("/api/customer/wishlist/*")
public class ApiWishlistServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final ManageWishlistUseCase manageWishlistUseCase = new ManageWishlistUseCase();

    @Override
    public void init() {
        System.out.println("✅ ApiWishlistServlet initialized");
    }

    // GET - Get wishlist
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
            List<WishlistItemDTO> wishlist = manageWishlistUseCase.getWishlist(userId);

            ApiResponse<List<WishlistItemDTO>> response = ApiResponse.success(
                    "Wishlist retrieved successfully",
                    wishlist
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to get wishlist: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // POST - Add to wishlist
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
            String requestBody = readRequestBody(req);
            Map<String, String> request = gson.fromJson(requestBody, Map.class);
            String productCode = request.get("productCode");

            if (productCode == null || productCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Product code is required");
            }

            Wishlist wishlist = manageWishlistUseCase.addToWishlist(userId, productCode);

            ApiResponse<Wishlist> response = ApiResponse.success(
                    "Added to wishlist",
                    wishlist
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to add to wishlist: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // DELETE - Remove from wishlist
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
            String productCode = extractProductCode(pathInfo);

            if (productCode == null || productCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Product code is required");
            }

            manageWishlistUseCase.removeFromWishlist(userId, productCode);

            ApiResponse<Object> response = ApiResponse.success("Removed from wishlist", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to remove from wishlist: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // Helper: Extract product code from path
    private String extractProductCode(String pathInfo) {
        if (pathInfo != null && pathInfo.length() > 1) {
            return pathInfo.substring(1);
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