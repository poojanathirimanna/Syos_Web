package com.syos.web.application.usecases;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.BillItemDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.domain.model.Product;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case for creating a bill/invoice
 * 🆕 NOW WITH AUTOMATIC DISCOUNT APPLICATION!
 */
public class CreateBillUseCase {

    private final BillDao billDao;
    private final ProductDao productDao;

    public CreateBillUseCase(BillDao billDao, ProductDao productDao) {
        this.billDao = billDao;
        this.productDao = productDao;
    }

    public BillDTO execute(CreateBillRequest request, String userId) {
        request.validate();

        try {
            // 🆕 Calculate totals WITH DISCOUNTS
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;  // 🆕 NEW - Track total discount
            List<BillItemDTO> billItems = new ArrayList<>();

            for (CreateBillRequest.BillItem item : request.getItems()) {
                var productOpt = productDao.findByProductCode(item.getProductCode());

                if (productOpt.isEmpty()) {
                    throw new IllegalArgumentException("Product not found: " + item.getProductCode());
                }

                Product product = productOpt.get();

                // 🆕 NEW - Use discounted price if active discount exists
                BigDecimal priceAtSale = product.hasActiveDiscount() ?
                        product.getDiscountedPrice() : product.getUnitPrice();

                // 🆕 NEW - Calculate item discount amount
                BigDecimal itemDiscount = BigDecimal.ZERO;
                if (product.hasActiveDiscount()) {
                    BigDecimal originalItemTotal = product.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    BigDecimal discountedItemTotal = priceAtSale
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    itemDiscount = originalItemTotal.subtract(discountedItemTotal);

                    System.out.println("🎯 Discount applied to " + product.getName() + ": " +
                            product.getDiscountPercentage() + "% off (Rs. " + itemDiscount + ")");
                }

                BigDecimal itemTotal = priceAtSale.multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
                totalDiscount = totalDiscount.add(itemDiscount);  // 🆕 NEW

                billItems.add(new BillItemDTO(
                        null,
                        item.getProductCode(),
                        product.getName(),
                        item.getQuantity(),
                        priceAtSale,  // 🆕 Shows discounted price!
                        itemTotal
                ));
            }

            BigDecimal totalAmount = subtotal;  // Subtotal already includes discount

            // Calculate change
            BigDecimal amountPaid = request.getAmountPaid();
            BigDecimal changeAmount = BigDecimal.ZERO;

            if (amountPaid != null) {
                // Validate amount paid is enough
                if (amountPaid.compareTo(totalAmount) < 0) {
                    throw new IllegalArgumentException(
                            "Amount paid (Rs. " + amountPaid + ") is less than total amount (Rs. " + totalAmount + ")"
                    );
                }
                changeAmount = amountPaid.subtract(totalAmount);
            }

            // 🆕 Create bill with discount amount
            String billNumber = billDao.createBill(
                    userId,
                    request.getPaymentMethod(),
                    subtotal,
                    totalDiscount,   // 🆕 NOW HAS ACTUAL DISCOUNT!
                    totalAmount,
                    amountPaid,
                    changeAmount
            );

            // Add items and deduct stock
            for (int i = 0; i < request.getItems().size(); i++) {
                CreateBillRequest.BillItem requestItem = request.getItems().get(i);
                BillItemDTO billItem = billItems.get(i);

                billDao.addBillItem(
                        billNumber,
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billItem.getUnitPrice(),
                        BigDecimal.ZERO
                );

                int batchIdUsed = billDao.deductStockForSale(
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billNumber,
                        userId
                );

                System.out.println("Product " + requestItem.getProductCode() +
                        " deducted from batch_id: " + batchIdUsed);
            }

            BillDTO finalBill = billDao.getBillByNumber(billNumber);

            // 🆕 Log discount summary
            if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("💰 Total discount saved: Rs. " + totalDiscount);
            }

            return finalBill;

        } catch (SQLException e) {
            System.err.println("SQL Error in CreateBillUseCase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create bill: " + e.getMessage(), e);
        }
    }
}