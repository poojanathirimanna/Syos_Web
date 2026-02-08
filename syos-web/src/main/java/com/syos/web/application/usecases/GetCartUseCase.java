package com.syos.web.application.usecases;

import com.syos.web.application.dto.CartItemDTO;
import com.syos.web.application.dto.CartResponseDTO;
import com.syos.web.application.dto.CartSummaryDTO;
import com.syos.web.infrastructure.persistence.dao.ShoppingCartDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Use Case: Get Shopping Cart
 * Retrieves user's cart with items and summary
 */
public class GetCartUseCase {

    private final ShoppingCartDao cartDao;

    public GetCartUseCase() {
        this.cartDao = new ShoppingCartDao();
    }

    /**
     * Execute: Get cart for user
     */
    public CartResponseDTO execute(String userId) throws SQLException {
        // Get cart items with product details
        List<CartItemDTO> items = cartDao.getCartItems(userId);

        // Calculate summary
        int itemCount = items.size();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItemDTO item : items) {
            subtotal = subtotal.add(item.getSubtotal());

            // Calculate discount for this item
            if (item.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal itemOriginalTotal = item.getOriginalPrice().multiply(new BigDecimal(item.getQuantity()));
                BigDecimal itemDiscount = itemOriginalTotal.subtract(item.getSubtotal());
                totalDiscount = totalDiscount.add(itemDiscount);
            }
        }

        BigDecimal totalAmount = subtotal;

        CartSummaryDTO summary = new CartSummaryDTO(
                itemCount,
                subtotal,
                totalDiscount,
                totalAmount
        );

        return new CartResponseDTO(items, summary);
    }
}