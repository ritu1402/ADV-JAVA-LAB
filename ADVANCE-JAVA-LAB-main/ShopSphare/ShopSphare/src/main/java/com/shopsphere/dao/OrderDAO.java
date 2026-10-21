package com.shopsphere.dao;

import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;
import com.shopsphere.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO {

    private static final String SQL_INSERT_ORDER = 
        "INSERT INTO orders (order_number, user_id, shipping_address_id, billing_address_id, " +
        "coupon_id, order_status, subtotal, discount_amount, shipping_fee, tax_amount, total_amount, notes) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_ORDER_ITEM = 
        "INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID = 
        "SELECT order_id, order_number, user_id, shipping_address_id, billing_address_id, coupon_id, " +
        "order_status, subtotal, discount_amount, shipping_fee, tax_amount, total_amount, notes, created_at, updated_at " +
        "FROM orders WHERE order_id = ?";

    private static final String SQL_FIND_BY_ORDER_NUMBER = 
        "SELECT order_id, order_number, user_id, shipping_address_id, billing_address_id, coupon_id, " +
        "order_status, subtotal, discount_amount, shipping_fee, tax_amount, total_amount, notes, created_at, updated_at " +
        "FROM orders WHERE order_number = ?";

    private static final String SQL_FIND_BY_USER_ID = 
        "SELECT order_id, order_number, user_id, shipping_address_id, billing_address_id, coupon_id, " +
        "order_status, subtotal, discount_amount, shipping_fee, tax_amount, total_amount, notes, created_at, updated_at " +
        "FROM orders WHERE user_id = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_ITEMS_BY_ORDER_ID = 
        "SELECT order_item_id, order_id, product_id, quantity, unit_price, total_price, created_at " +
        "FROM order_items WHERE order_id = ?";

    private static final String SQL_UPDATE_ORDER_STATUS = 
        "UPDATE orders SET order_status = ? WHERE order_id = ?";

    public Order createOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(SQL_INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderNumber());
                psOrder.setLong(2, order.getUserId());

                if (order.getShippingAddressId() != null) {
                    psOrder.setLong(3, order.getShippingAddressId());
                } else {
                    psOrder.setNull(3, Types.BIGINT);
                }

                if (order.getBillingAddressId() != null) {
                    psOrder.setLong(4, order.getBillingAddressId());
                } else {
                    psOrder.setNull(4, Types.BIGINT);
                }

                if (order.getCouponId() != null) {
                    psOrder.setInt(5, order.getCouponId());
                } else {
                    psOrder.setNull(5, Types.INTEGER);
                }

                psOrder.setString(6, order.getOrderStatus() != null ? order.getOrderStatus() : "pending");
                psOrder.setBigDecimal(7, order.getSubtotal());
                psOrder.setBigDecimal(8, order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
                psOrder.setBigDecimal(9, order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO);
                psOrder.setBigDecimal(10, order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO);
                psOrder.setBigDecimal(11, order.getTotalAmount());
                psOrder.setString(12, order.getNotes());

                psOrder.executeUpdate();

                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating order failed, no generated order ID obtained.");
                    }
                }
            }

            if (items != null && !items.isEmpty()) {
                try (PreparedStatement psItems = conn.prepareStatement(SQL_INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS)) {
                    for (OrderItem item : items) {
                        psItems.setLong(1, order.getOrderId());
                        psItems.setLong(2, item.getProductId());
                        psItems.setInt(3, item.getQuantity());
                        psItems.setBigDecimal(4, item.getUnitPrice());
                        psItems.setBigDecimal(5, item.getTotalPrice());
                        psItems.addBatch();
                    }
                    psItems.executeBatch();
                }
            }

            conn.commit();
            return order;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    // Suppress or log
                }
            }
        }
    }

    public Optional<Order> findById(Long orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Order> findByOrderNumber(String orderNumber) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ORDER_NUMBER)) {

            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Order> findByUserId(Long userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    public List<OrderItem> findOrderItems(Long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ITEMS_BY_ORDER_ID)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        }
        return items;
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ORDER_STATUS)) {

            ps.setString(1, newStatus);
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        long shipAddrVal = rs.getLong("shipping_address_id");
        Long shippingAddressId = rs.wasNull() ? null : shipAddrVal;

        long billAddrVal = rs.getLong("billing_address_id");
        Long billingAddressId = rs.wasNull() ? null : billAddrVal;

        int couponVal = rs.getInt("coupon_id");
        Integer couponId = rs.wasNull() ? null : couponVal;

        return new Order(
            rs.getLong("order_id"),
            rs.getString("order_number"),
            rs.getLong("user_id"),
            shippingAddressId,
            billingAddressId,
            couponId,
            rs.getString("order_status"),
            rs.getBigDecimal("subtotal"),
            rs.getBigDecimal("discount_amount"),
            rs.getBigDecimal("shipping_fee"),
            rs.getBigDecimal("tax_amount"),
            rs.getBigDecimal("total_amount"),
            rs.getString("notes"),
            createdTs != null ? createdTs.toLocalDateTime() : null,
            updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");

        return new OrderItem(
            rs.getLong("order_item_id"),
            rs.getLong("order_id"),
            rs.getLong("product_id"),
            rs.getInt("quantity"),
            rs.getBigDecimal("unit_price"),
            rs.getBigDecimal("total_price"),
            createdTs != null ? createdTs.toLocalDateTime() : null
        );
    }
}

