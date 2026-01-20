package com.syos.web.application.ports;

import com.syos.web.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product operations
 * Following Dependency Inversion Principle (DIP)
 * Application layer defines the interface, Infrastructure implements it
 */
public interface IProductRepository {

    /**
     * Get all products from the database
     * @return List of all products
     */
    List<Product> findAll();

    /**
     * Find a product by its product code
     * @param productCode The product code
     * @return Optional containing the product if found
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * Check if a product exists by product code
     * @param productCode The product code
     * @return true if product exists, false otherwise
     */
    boolean existsByProductCode(String productCode);

    /**
     * Save a new product
     * @param product The product to save
     * @return The saved product
     */
    Product save(Product product);

    /**
     * Update an existing product
     * @param product The product with updated information
     * @return The updated product
     */
    Product update(Product product);

    /**
     * Delete a product by product code
     * @param productCode The product code
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteByProductCode(String productCode);

    /**
     * Find products with low stock (warehouse quantity < threshold)
     * @param threshold The stock threshold
     * @return List of products with low stock
     */
    List<Product> findLowStockProducts(int threshold);

    /**
     * Find products that are out of stock (shelf + warehouse = 0)
     * @return List of out of stock products
     */
    List<Product> findOutOfStockProducts();

    /**
     * Get total count of products
     * @return Total number of products
     */
    int count();
}