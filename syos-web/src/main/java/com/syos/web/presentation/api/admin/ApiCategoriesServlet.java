package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Categories Servlet - Full CRUD for categories
 * GET    /api/admin/categories     → List all
 * POST   /api/admin/categories     → Create
 * PUT    /api/admin/categories     → Update
 * DELETE /api/admin/categories/{id} → Delete
 */
@WebServlet("/api/admin/categories/*")  // ⭐ Changed: Added /*
public class ApiCategoriesServlet extends HttpServlet {

    private final Gson gson = new Gson();

    // ===================================================================
    // GET - List all categories
    // ===================================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Map<String, Object>> categories = new ArrayList<>();

            String sql = "SELECT category_id, category_name, description FROM product_categories ORDER BY category_name";

            try (Connection conn = Db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();
                    category.put("categoryId", rs.getInt("category_id"));
                    category.put("categoryName", rs.getString("category_name"));
                    category.put("description", rs.getString("description"));
                    categories.add(category);
                }
            }

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                    "Categories retrieved successfully",
                    categories
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve categories: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // POST - Create new category
    // ===================================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = gson.fromJson(requestBody, Map.class);

            String categoryName = (String) requestData.get("categoryName");
            String description = (String) requestData.get("description");

            // Validation
            if (categoryName == null || categoryName.trim().isEmpty()) {
                ApiResponse<Object> error = ApiResponse.error("Category name is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Insert category
            String sql = "INSERT INTO product_categories (category_name, description) VALUES (?, ?)";

            try (Connection conn = Db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, categoryName.trim());
                stmt.setString(2, description != null ? description.trim() : null);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Get generated ID
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int categoryId = rs.getInt(1);

                            Map<String, Object> createdCategory = new HashMap<>();
                            createdCategory.put("categoryId", categoryId);
                            createdCategory.put("categoryName", categoryName);
                            createdCategory.put("description", description);

                            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                                    "Category created successfully",
                                    createdCategory
                            );

                            resp.setStatus(HttpServletResponse.SC_CREATED);
                            resp.getWriter().write(gson.toJson(response));
                        }
                    }
                }
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to create category: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // PUT - Update category
    // ===================================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = gson.fromJson(requestBody, Map.class);

            Double categoryIdDouble = (Double) requestData.get("categoryId");
            int categoryId = categoryIdDouble.intValue();
            String categoryName = (String) requestData.get("categoryName");
            String description = (String) requestData.get("description");

            // Validation
            if (categoryName == null || categoryName.trim().isEmpty()) {
                ApiResponse<Object> error = ApiResponse.error("Category name is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Update category
            String sql = "UPDATE product_categories SET category_name = ?, description = ? WHERE category_id = ?";

            try (Connection conn = Db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, categoryName.trim());
                stmt.setString(2, description != null ? description.trim() : null);
                stmt.setInt(3, categoryId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    Map<String, Object> updatedCategory = new HashMap<>();
                    updatedCategory.put("categoryId", categoryId);
                    updatedCategory.put("categoryName", categoryName);
                    updatedCategory.put("description", description);

                    ApiResponse<Map<String, Object>> response = ApiResponse.success(
                            "Category updated successfully",
                            updatedCategory
                    );

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(response));
                } else {
                    ApiResponse<Object> error = ApiResponse.error("Category not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error));
                }
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to update category: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // DELETE - Delete category
    // ===================================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                ApiResponse<Object> error = ApiResponse.error("Category ID is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            int categoryId = Integer.parseInt(pathInfo.substring(1));

            // Check if category is being used by products
            String checkSql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND is_deleted = FALSE";

            try (Connection conn = Db.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setInt(1, categoryId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        int productCount = rs.getInt(1);
                        ApiResponse<Object> error = ApiResponse.error(
                                "Cannot delete category: It is being used by " + productCount + " product(s)"
                        );
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(gson.toJson(error));
                        return;
                    }
                }

                // Delete category
                String deleteSql = "DELETE FROM product_categories WHERE category_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, categoryId);
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        ApiResponse<Object> response = ApiResponse.success(
                                "Category deleted successfully",
                                null
                        );
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(gson.toJson(response));
                    } else {
                        ApiResponse<Object> error = ApiResponse.error("Category not found");
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(gson.toJson(error));
                    }
                }
            }

        } catch (NumberFormatException e) {
            ApiResponse<Object> error = ApiResponse.error("Invalid category ID");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to delete category: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // Helper method
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

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}