package com.syos.web.application.usecases;

import com.syos.web.application.dto.WishlistItemDTO;
import com.syos.web.domain.model.Wishlist;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.WishlistDao;

import java.sql.SQLException;
import java.util.List;

/**
 * Use Case: Manage Wishlist
 * Handles all wishlist operations
 */
public class ManageWishlistUseCase {

    private final WishlistDao wishlistDao;
    private final ProductDao productDao;

    public ManageWishlistUseCase() {
        this.wishlistDao = new WishlistDao();
        this.productDao = new ProductDao();
    }

    /**
     * Get wishlist items
     */
    public List<WishlistItemDTO> getWishlist(String userId) throws SQLException {
        return wishlistDao.getWishlistItems(userId);
    }

    /**
     * Add to wishlist
     */
    public Wishlist addToWishlist(String userId, String productCode) throws SQLException {
        // Check if product exists
        if (productDao.findByCode(productCode).isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }

        // Check if already in wishlist
        if (wishlistDao.isInWishlist(userId, productCode)) {
            throw new IllegalArgumentException("Product already in wishlist");
        }

        return wishlistDao.addToWishlist(userId, productCode);
    }

    /**
     * Remove from wishlist
     */
    public boolean removeFromWishlist(String userId, String productCode) throws SQLException {
        boolean removed = wishlistDao.removeFromWishlist(userId, productCode);

        if (!removed) {
            throw new IllegalArgumentException("Product not in wishlist");
        }

        return true;
    }

    /**
     * Check if in wishlist
     */
    public boolean isInWishlist(String userId, String productCode) throws SQLException {
        return wishlistDao.isInWishlist(userId, productCode);
    }
}