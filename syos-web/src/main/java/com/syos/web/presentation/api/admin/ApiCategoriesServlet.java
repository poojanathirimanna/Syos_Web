package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Categories Servlet - Simple endpoint to list all categories
 * GET /api/admin/categories
 */
@WebServlet("/api/admin/categories")
public class ApiCategoriesServlet extends HttpServlet {

    private final Gson gson = new Gson();

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

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}