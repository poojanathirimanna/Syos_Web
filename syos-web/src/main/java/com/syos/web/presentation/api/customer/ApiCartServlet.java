package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.*;
import com.syos.web.application.usecases.*;
import com.syos.web.domain.model.ShoppingCart;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Shopping Cart Servlet
 * Handles all cart operations for customers
 *
 * GET    /api/customer/cart           → Get cart
 * POST   /api/customer/cart           → Add to cart
 * PUT    /api/customer/cart/{cartId}  → Update cart item
 * DELETE /api/customer/cart/{cartId}  → Remove from cart
 * DELETE /api/customer/cart           → Clear cart
 */
@WebServlet("/api/customer/cart/*")
public class ApiCartServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final GetCartUseCase getCartUseCase = new GetCartUseCase();
    private final AddToCartUseCase addToCartUseCase = new AddToCartUseCase();
    private final UpdateCartUseCase updateCartUseCase = new UpdateCartUseCase();
    private final RemoveFromCartUseCase removeFromCartUseCase = new RemoveFromCartUseCase();
    private final ClearCartUseCase clearCartUseCase = new ClearCartUseCase();

    @Override
    public void init() {
        System.out.println("✅ ApiCartServlet initialized");
    }

    // GET - Get cart
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            CartResponseDTO cart = getCartUseCase.execute(userId);

            ApiResponse<CartResponseDTO> response = ApiResponse.success(
                    "Cart retrieved successfully",
                    cart
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to get cart: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // POST - Add to cart
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String requestBody = readRequestBody(req);
            AddToCartRequest request = gson.fromJson(requestBody, AddToCartRequest.class);

            ShoppingCart cart = addToCartUseCase.execute(userId, request);

            ApiResponse<ShoppingCart> response = ApiResponse.success(
                    "Product added to cart",
                    cart
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to add to cart: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // PUT - Update cart item
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String pathInfo = req.getPathInfo();
            Integer cartId = extractCartId(pathInfo);

            if (cartId == null) {
                throw new IllegalArgumentException("Cart ID is required");
            }

            String requestBody = readRequestBody(req);
            UpdateCartRequest request = gson.fromJson(requestBody, UpdateCartRequest.class);

            updateCartUseCase.execute(cartId, userId, request);

            ApiResponse<Object> response = ApiResponse.success("Cart updated successfully", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update cart: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // DELETE - Remove from cart or clear cart
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        try {
            String pathInfo = req.getPathInfo();

            // If no path info, clear entire cart
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                clearCartUseCase.execute(userId);

                ApiResponse<Object> response = ApiResponse.success("Cart cleared successfully", null);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            // Otherwise, remove specific item
            Integer cartId = extractCartId(pathInfo);
            if (cartId == null) {
                throw new IllegalArgumentException("Cart ID is required");
            }

            removeFromCartUseCase.execute(cartId, userId);

            ApiResponse<Object> response = ApiResponse.success("Item removed from cart", null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            ApiResponse<Object> error = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to remove from cart: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // Helper: Extract cart ID from path
    private Integer extractCartId(String pathInfo) {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                return Integer.parseInt(pathInfo.substring(1));
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