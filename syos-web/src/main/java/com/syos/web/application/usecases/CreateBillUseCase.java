package com.syos.web.application.usecases;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.BillItemDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case for creating a bill/invoice
 * Handles POS (Point of Sale) transactions
 */
public class CreateBillUseCase {

    private final BillDao billDao;
    private final ProductDao productDao;

    public CreateBillUseCase(BillDao billDao, ProductDao productDao) {
        this.billDao = billDao;
        this.productDao = productDao;
    }

    /**
     * Execute: Create bill with items and deduct stock using FEFO
     *
     * @param request - CreateBillRequest with items and payment method
     * @param userId - Cashier ID (from session)
     * @return BillDTO - Created bill with all details
     */
    public BillDTO execute(CreateBillRequest request, String userId) {
        // Step 1: Validate request
        request.validate();

        try {
            // Step 2: Calculate totals and validate products
            BigDecimal subtotal = BigDecimal.ZERO;
            List<BillItemDTO> billItems = new ArrayList<>();

            for (CreateBillRequest.BillItem item : request.getItems()) {
                // Get product details
                var product = productDao.findByProductCode(item.getProductCode());

                if (product.isEmpty()) {
                    throw new IllegalArgumentException("Product not found: " + item.getProductCode());
                }

                // Get current price
                BigDecimal priceAtSale = product.get().getUnitPrice();

                // Calculate item total
                BigDecimal itemTotal = priceAtSale.multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);

                // Prepare bill item DTO
                billItems.add(new BillItemDTO(
                        null,  // bill_item_id will be auto-generated
                        item.getProductCode(),
                        product.get().getName(),
                        item.getQuantity(),
                        priceAtSale,
                        itemTotal
                ));
            }

            // Step 3: Calculate discount and total
            BigDecimal discountAmount = BigDecimal.ZERO;  // No discount for now (can be extended)
            BigDecimal totalAmount = subtotal.subtract(discountAmount);

            // Step 4: Create bill in database (returns bill_number)
            String billNumber = billDao.createBill(
                    userId,                      // cashier_id
                    request.getPaymentMethod(),  // channel (CASH, CARD, etc.)
                    subtotal,
                    discountAmount,
                    totalAmount
            );

            // Step 5: Add items and deduct stock
            for (int i = 0; i < request.getItems().size(); i++) {
                CreateBillRequest.BillItem requestItem = request.getItems().get(i);
                BillItemDTO billItem = billItems.get(i);

                // Add bill item to database
                billDao.addBillItem(
                        billNumber,
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billItem.getUnitPrice(),     // price_at_sale
                        BigDecimal.ZERO              // discount_applied (no discount per item)
                );

                // Deduct from inventory using FEFO stored procedure
                // This automatically:
                // - Picks oldest batches first (FEFO)
                // - Deducts from SHELF location
                // - Records in stock_movements table
                billDao.deductStockForSale(
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billNumber,
                        userId
                );
            }

            // Step 6: Return created bill with full details
            return billDao.getBillByNumber(billNumber);

        } catch (SQLException e) {
            // Log error and throw runtime exception
            System.err.println("SQL Error in CreateBillUseCase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create bill: " + e.getMessage(), e);
        }
    }
}