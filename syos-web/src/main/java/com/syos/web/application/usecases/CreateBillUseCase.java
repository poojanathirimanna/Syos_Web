package com.syos.web.application.usecases;

import com.syos.web.application.dto.BillDTO;
import com.syos.web.application.dto.BillItemDTO;
import com.syos.web.application.dto.CreateBillRequest;
import com.syos.web.domain.model.Product;
import com.syos.web.infrastructure.persistence.dao.BillDao;
import com.syos.web.infrastructure.persistence.dao.ProductDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use Case for creating a bill/invoice
 * 🆕 NOW HANDLES BOTH CASHIER BILLS AND CUSTOMER ORDERS!
 */
public class CreateBillUseCase {

    private final BillDao billDao;
    private final ProductDao productDao;

    public CreateBillUseCase(BillDao billDao, ProductDao productDao) {
        this.billDao = billDao;
        this.productDao = productDao;
    }

    /**
     * Execute bill creation (CASHIER - backward compatible)
     */
    public BillDTO execute(CreateBillRequest request, String userId) {
        return execute(request, userId, "CASHIER");
    }

    /**
     * 🆕 Execute bill creation (SUPPORTS BOTH CASHIER AND CUSTOMER)
     */
    public BillDTO execute(CreateBillRequest request, String userId, String userType) {
        request.validate();

        try {
            // Determine channel
            String channel = request.getChannel() != null ? request.getChannel() : "IN_STORE";

            System.out.println("🔍 DEBUG CreateBillUseCase.execute:");
            System.out.println("   User ID: '" + userId + "'");
            System.out.println("   User Type: '" + userType + "'");
            System.out.println("   Channel from request: '" + request.getChannel() + "'");
            System.out.println("   Channel (after default): '" + channel + "'");
            System.out.println("   Payment Method: '" + request.getPaymentMethod() + "'");

            // Log which type of bill we're creating
            System.out.println("🔨 Creating " + channel + " bill for " + userType + ": " + userId);

            // Calculate totals WITH DISCOUNTS
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            List<BillItemDTO> billItems = new ArrayList<>();

            for (CreateBillRequest.BillItemRequest item : request.getItems()) {
                var productOpt = productDao.findByProductCode(item.getProductCode());

                if (productOpt.isEmpty()) {
                    throw new IllegalArgumentException("Product not found: " + item.getProductCode());
                }

                Product product = productOpt.get();

                // Check if product is deleted
                if (product.isDeleted()) {
                    throw new IllegalArgumentException("Product is no longer available: " + item.getProductCode());
                }

                // Check stock availability - USE WEBSITE for ONLINE, SHELF for IN_STORE
                int availableStock = "ONLINE".equals(channel) ?
                        product.getWebsiteQuantity() : product.getShelfQuantity();

                if (availableStock < item.getQuantity()) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for " + product.getName() +
                                    ". Available: " + availableStock + ", Requested: " + item.getQuantity()
                    );
                }

                // Use discounted price if active discount exists
                BigDecimal priceAtSale = product.hasActiveDiscount() ?
                        product.getDiscountedPrice() : product.getUnitPrice();

                // Calculate item discount amount
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
                totalDiscount = totalDiscount.add(itemDiscount);

                billItems.add(new BillItemDTO(
                        null,
                        item.getProductCode(),
                        product.getName(),
                        item.getQuantity(),
                        priceAtSale,
                        itemTotal
                ));
            }

            BigDecimal totalAmount = subtotal;

            // Calculate change for cash payments
            BigDecimal amountPaid = request.getAmountPaid();
            BigDecimal changeAmount = BigDecimal.ZERO;

            if (amountPaid != null) {
                if (amountPaid.compareTo(totalAmount) < 0) {
                    throw new IllegalArgumentException(
                            "Amount paid (Rs. " + amountPaid + ") is less than total amount (Rs. " + totalAmount + ")"
                    );
                }
                changeAmount = amountPaid.subtract(totalAmount);
            } else {
                amountPaid = totalAmount; // For card/digital payments
            }

            // 🆕 Generate tracking number for online orders
            String trackingNumber = null;
            LocalDate estimatedDeliveryDate = null;
            if ("ONLINE".equals(channel)) {
                trackingNumber = generateTrackingNumber();
                estimatedDeliveryDate = LocalDate.now().plusDays(5); // 5 days delivery
            }

            // 🆕 Determine order and payment status
            String orderStatus = "ONLINE".equals(channel) ?
                    (request.getOrderStatus() != null ? request.getOrderStatus() : "PENDING") :
                    null;

            String paymentStatus = "ONLINE".equals(channel) ?
                    (request.getPaymentStatus() != null ? request.getPaymentStatus() : "PENDING") :
                    null;

            // 🆕 Create bill with customer order support
            String billNumber = billDao.createBill(
                    userId,
                    userType,
                    channel,
                    request.getPaymentMethod(),
                    subtotal,
                    totalDiscount,
                    totalAmount,
                    amountPaid,
                    changeAmount,
                    // Customer order fields
                    request.getDeliveryAddress(),
                    request.getDeliveryCity(),
                    request.getDeliveryPostalCode(),
                    request.getDeliveryPhone(),
                    request.getPaymentMethodDetails(),
                    orderStatus,
                    paymentStatus,
                    trackingNumber,
                    estimatedDeliveryDate
            );

            System.out.println("✅ Bill created: " + billNumber + " | Channel: " + channel);

            // Add items and deduct stock
            for (int i = 0; i < request.getItems().size(); i++) {
                CreateBillRequest.BillItemRequest requestItem = request.getItems().get(i);
                BillItemDTO billItem = billItems.get(i);

                billDao.addBillItem(
                        billNumber,
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billItem.getUnitPrice(),
                        BigDecimal.ZERO
                );

                // Deduct from correct location based on channel
                String deductionLocation = "ONLINE".equals(channel) ? "WEBSITE" : "SHELF";

                int batchIdUsed = billDao.deductStockForSale(
                        requestItem.getProductCode(),
                        requestItem.getQuantity(),
                        billNumber,
                        userId,
                        deductionLocation
                );

                System.out.println("Product " + requestItem.getProductCode() +
                        " deducted from " + deductionLocation + " batch_id: " + batchIdUsed);
            }

            BillDTO finalBill = billDao.getBillByNumber(billNumber);

            // Log discount summary
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

    /**
     * 🆕 Generate tracking number for online orders
     */
    private String generateTrackingNumber() {
        return "TRACK-" + LocalDate.now().toString().replace("-", "") + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}