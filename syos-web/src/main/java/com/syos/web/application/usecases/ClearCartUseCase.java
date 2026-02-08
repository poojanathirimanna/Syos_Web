package com.syos.web.application.usecases;

import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;

import java.sql.SQLException;

/**
 * Use Case: Clear Cart
 * Removes all items from cart
 */
public class ClearCartUseCase {

    private final ShoppingCartDao cartDao;

    public ClearCartUseCase() {
        this.cartDao = new ShoppingCartDao();
    }

    /**
     * Execute: Clear entire cart
     */
    public boolean execute(String userId) throws SQLException {
        cartDao.clearCart(userId);
        System.out.println("🗑️ Cart cleared for user: " + userId);
        return true;
    }
}