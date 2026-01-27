package com.syos.web.infrastructure.persistence.dao;

import com.syos.web.application.dto.AvailableProductDTO;
import com.syos.web.db.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for fetching products available for sale (SHELF location only)
 */
public class AvailableProductsDao {

    /**
     * Get all products available on SHELF (aggregated across all batches)
     */
    public List<AvailableProductDTO> getAvailableProducts() throws SQLException {
        List<AvailableProductDTO> products = new ArrayList<>();

        String sql = "SELECT " +
                "    p.product_code, " +
                "    p.name, " +
                "    pc.category_name, " +
                "    p.unit_price, " +
                "    p.image_url, " +
                "    COALESCE(SUM(il.quantity), 0) as available_quantity " +
                "FROM products p " +
                "LEFT JOIN product_categories pc ON p.category_id = pc.category_id " +
                "LEFT JOIN inventory_locations il ON p.product_code = il.product_code AND il.location = 'SHELF' " +
                "WHERE p.is_deleted = FALSE " +
                "GROUP BY p.product_code, p.name, pc.category_name, p.unit_price, p.image_url " +
                "HAVING available_quantity > 0 " +
                "ORDER BY p.name";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new AvailableProductDTO(
                        rs.getString("product_code"),
                        rs.getString("name"),
                        rs.getString("category_name"),
                        rs.getBigDecimal("unit_price"),
                        rs.getInt("available_quantity"),
                        rs.getString("image_url")
                ));
            }
        }

        return products;
    }
}