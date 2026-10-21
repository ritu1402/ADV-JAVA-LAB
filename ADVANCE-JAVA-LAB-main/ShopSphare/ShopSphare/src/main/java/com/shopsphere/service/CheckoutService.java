package com.shopsphere.service;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.dao.CouponDAO;
import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.PaymentDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.CartItem;
import com.shopsphere.model.Coupon;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.model.Payment;
import com.shopsphere.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CheckoutService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8% sales tax
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("10.00");

    private final CartDAO cartDAO;
    private final CouponDAO couponDAO;
    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;
    private final ProductDAO productDAO;

    public CheckoutService() {
        this.cartDAO = new CartDAO();
        this.couponDAO = new CouponDAO();
        this.orderDAO = new OrderDAO();
        this.paymentDAO = new PaymentDAO();
        this.productDAO = new ProductDAO();
    }

    public CheckoutService(CartDAO cartDAO, CouponDAO couponDAO, OrderDAO orderDAO,
                           PaymentDAO paymentDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.couponDAO = couponDAO;
        this.orderDAO = orderDAO;
        this.paymentDAO = paymentDAO;
        this.productDAO = productDAO;
    }

    /**
     * Executes the checkout process with strict server-side calculation of all pricing,
     * taxes, discounts, and order totals. No client-supplied financial values are trusted.
     */
    public CheckoutResult processCheckout(CheckoutRequest request) throws SQLException {
        if (request == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Invalid checkout request: user ID is required.");
        }

        Long userId = request.getUserId();

        // 1. Fetch user cart items from database
        List<CartItem> cartItems = cartDAO.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Checkout failed: Shopping cart is empty.");
        }

        // 2. Fetch authoritative database products and calculate Subtotal strictly on server
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Optional<Product> productOpt = productDAO.findById(cartItem.getProductId());
            if (productOpt.isEmpty()) {
                throw new IllegalStateException("Product ID " + cartItem.getProductId() + " no longer exists.");
            }

            Product product = productOpt.get();
            if (!"active".equalsIgnoreCase(product.getStatus())) {
                throw new IllegalStateException("Product '" + product.getName() + "' is not currently available for purchase.");
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product '" + product.getName() + 
                                                "'. Available: " + product.getStockQuantity() + 
                                                ", Requested: " + cartItem.getQuantity());
            }

            // Determine authoritative unit price: use active sale_price if present and lower than price
            BigDecimal unitPrice = product.getPrice();
            if (product.getSalePrice() != null && 
                product.getSalePrice().compareTo(BigDecimal.ZERO) > 0 &&
                product.getSalePrice().compareTo(product.getPrice()) < 0) {
                unitPrice = product.getSalePrice();
            }

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                                            .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(lineTotal);
            orderItems.add(orderItem);

            productsToUpdate.add(product);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        // 3. Server-side Coupon validation & Discount calculation
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon appliedCoupon = null;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            String sanitizedCode = request.getCouponCode().trim();
            Optional<Coupon> couponOpt = couponDAO.findByCode(sanitizedCode);

            if (couponOpt.isEmpty()) {
                throw new IllegalArgumentException("Coupon code '" + sanitizedCode + "' is invalid.");
            }

            Coupon coupon = couponOpt.get();
            validateCouponEligibility(coupon, userId, subtotal);

            // Compute discount value
            if ("percentage".equalsIgnoreCase(coupon.getDiscountType())) {
                BigDecimal percent = coupon.getDiscountValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                discountAmount = subtotal.multiply(percent).setScale(2, RoundingMode.HALF_UP);

                // Apply maximum discount cap if defined
                if (coupon.getMaxDiscountAmount() != null && 
                    discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discountAmount = coupon.getMaxDiscountAmount();
                }
            } else if ("fixed_amount".equalsIgnoreCase(coupon.getDiscountType())) {
                discountAmount = coupon.getDiscountValue().setScale(2, RoundingMode.HALF_UP);
            }

            // Cap discount to not exceed subtotal
            if (discountAmount.compareTo(subtotal) > 0) {
                discountAmount = subtotal;
            }

            appliedCoupon = coupon;
        }

        // 4. Server-side Shipping & Tax calculation
        BigDecimal taxableSubtotal = subtotal.subtract(discountAmount);
        if (taxableSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            taxableSubtotal = BigDecimal.ZERO;
        }

        BigDecimal shippingFee = (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) 
            ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) 
            : STANDARD_SHIPPING_FEE.setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = taxableSubtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = taxableSubtotal.add(shippingFee).add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        // 5. Build and persist Order record
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setShippingAddressId(request.getShippingAddressId());
        order.setBillingAddressId(request.getBillingAddressId());
        order.setCouponId(appliedCoupon != null ? appliedCoupon.getCouponId() : null);
        order.setOrderStatus("processing");
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setNotes(request.getNotes());

        // Atomically create Order and OrderItems in database transaction
        Order createdOrder = orderDAO.createOrderWithItems(order, orderItems);

        // 6. Deduct product inventory
        for (OrderItem item : orderItems) {
            productDAO.updateStock(item.getProductId(), -item.getQuantity());
        }

        // 7. Audit coupon redemption if coupon applied
        if (appliedCoupon != null) {
            couponDAO.recordUsage(appliedCoupon.getCouponId(), userId, createdOrder.getOrderId(), discountAmount);
        }

        // 8. Process payment record
        Payment payment = new Payment();
        payment.setOrderId(createdOrder.getOrderId());
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "credit_card");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        payment.setAmount(totalAmount);
        payment.setPaymentStatus("completed");
        payment.setPaymentGatewayResponse("{\"status\":\"approved\",\"code\":\"00\"}");
        payment.setPaidAt(LocalDateTime.now());

        Payment createdPayment = paymentDAO.create(payment);

        // 9. Clear user's cart
        cartDAO.clearCart(userId);

        // 10. Return authoritative checkout summary
        return new CheckoutResult(
            createdOrder,
            createdPayment,
            orderItems,
            subtotal,
            discountAmount,
            shippingFee,
            taxAmount,
            totalAmount
        );
    }

    private void validateCouponEligibility(Coupon coupon, Long userId, BigDecimal subtotal) throws SQLException {
        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new IllegalArgumentException("Coupon code '" + coupon.getCode() + "' is inactive.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new IllegalArgumentException("Coupon code '" + coupon.getCode() + "' is not yet valid.");
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException("Coupon code '" + coupon.getCode() + "' has expired.");
        }

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("Order subtotal of $" + subtotal + 
                                               " is below minimum requirement of $" + coupon.getMinOrderAmount() + 
                                               " for coupon '" + coupon.getCode() + "'.");
        }

        if (coupon.getUsageLimit() != null) {
            int totalUsage = couponDAO.getTotalUsageCount(coupon.getCouponId());
            if (totalUsage >= coupon.getUsageLimit()) {
                throw new IllegalArgumentException("Coupon code '" + coupon.getCode() + "' has reached its total usage limit.");
            }
        }

        if (coupon.getUsagePerUser() != null) {
            int userUsage = couponDAO.getUserUsageCount(coupon.getCouponId(), userId);
            if (userUsage >= coupon.getUsagePerUser()) {
                throw new IllegalArgumentException("You have already reached the redemption limit for coupon '" + coupon.getCode() + "'.");
            }
        }
    }
}

