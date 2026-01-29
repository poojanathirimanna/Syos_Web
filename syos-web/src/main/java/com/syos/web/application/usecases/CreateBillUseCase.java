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
            // Calculate totals
            BigDecimal subtotal = BigDecimal.ZERO;
            List<BillItemDTO> billItems = new ArrayList<>();

            for (CreateBillRequest.BillItem item : request.getItems()) {
                var product = productDao.findByProductCode(item.getProductCode());

                if (product.isEmpty()) {
                    throw new IllegalArgumentException("Product not found: " + item.getProductCode());
                }

                BigDecimal priceAtSale = product.get().getUnitPrice();
                BigDecimal itemTotal = priceAtSale.multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);

                billItems.add(new BillItemDTO(
                        null,
                        item.getProductCode(),
                        product.get().getName(),
                        item.getQuantity(),
                        priceAtSale,
                        itemTotal
                ));
            }

            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = subtotal.subtract(discountAmount);

            // 🆕 CALCULATE CHANGE
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

            // Create bill with amount_paid and change_amount
            String billNumber = billDao.createBill(
                    userId,
                    request.getPaymentMethod(),
                    subtotal,
                    discountAmount,
                    totalAmount,
                    amountPaid,      // 🆕 NEW
                    changeAmount     // 🆕 NEW
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

            return billDao.getBillByNumber(billNumber);

        } catch (SQLException e) {
            System.err.println("SQL Error in CreateBillUseCase: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create bill: " + e.getMessage(), e);
        }
    }
}