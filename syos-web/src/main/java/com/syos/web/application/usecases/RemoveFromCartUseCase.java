package com.syos.web.application.usecases;

import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;

import java.sql.SQLException;

/**
 * Use Case: Remove from Cart
 * Removes item from shopping cart
 */
public class RemoveFromCartUseCase {

    private final ShoppingCartDao cartDao;

    public RemoveFromCartUseCase() {
        this.cartDao = new ShoppingCartDao();
    }

    /**
     * Execute: Remove cart item
     */
    public boolean execute(Integer cartId, String userId) throws SQLException {
        boolean removed = cartDao.removeItem(cartId, userId);

        if (!removed) {
            throw new IllegalArgumentException("Cart item not found or unauthorized");
        }

        System.out.println("🗑️ Removed from cart: ID=" + cartId);

        return true;
    }
}