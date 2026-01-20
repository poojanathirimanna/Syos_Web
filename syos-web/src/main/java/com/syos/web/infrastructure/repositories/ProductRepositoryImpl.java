package com.syos.web.infrastructure.repositories;

import com.syos.web.application.ports.IProductRepository;
import com.syos.web.domain.model.Product;
import com.syos.web.infrastructure.persistence.dao.ProductDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of IProductRepository
 * Adapts ProductDao to the repository interface
 * Following Dependency Inversion Principle
 */
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductDao productDao;

    // Constructor injection
    public ProductRepositoryImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    // Default constructor (creates ProductDao internally)
    public ProductRepositoryImpl() {
        this.productDao = new ProductDao();
    }

    @Override
    public List<Product> findAll() {
        try {
            return productDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findByProductCode(String productCode) {
        try {
            return productDao.findByProductCode(productCode);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByProductCode(String productCode) {
        try {
            return productDao.existsByProductCode(productCode);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check product existence: " + e.getMessage(), e);
        }
    }

    @Override
    public Product save(Product product) {
        try {
            // Validate before saving
            product.validate();
            return productDao.save(product);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product: " + e.getMessage(), e);
        }
    }

    @Override
    public Product update(Product product) {
        try {
            // Validate before updating
            product.validate();
            return productDao.update(product);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteByProductCode(String productCode) {
        try {
            return productDao.deleteByProductCode(productCode);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        try {
            return productDao.findLowStockProducts(threshold);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find low stock products: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> findOutOfStockProducts() {
        try {
            return productDao.findOutOfStockProducts();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find out of stock products: " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        try {
            return productDao.count();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count products: " + e.getMessage(), e);
        }
    }
}
