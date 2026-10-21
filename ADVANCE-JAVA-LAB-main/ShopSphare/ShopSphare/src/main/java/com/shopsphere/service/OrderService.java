package com.shopsphere.service;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Order;
import com.shopsphere.model.OrderItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
    }

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public Optional<Order> getOrderById(Long orderId) throws SQLException {
        if (orderId == null || orderId <= 0) {
            return Optional.empty();
        }
        return orderDAO.findById(orderId);
    }

    public Optional<Order> getOrderByNumber(String orderNumber) throws SQLException {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return orderDAO.findByOrderNumber(orderNumber.trim());
    }

    public List<Order> getUserOrders(Long userId) throws SQLException {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return orderDAO.findByUserId(userId);
    }

    public List<OrderItem> getOrderItems(Long orderId) throws SQLException {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Invalid order ID.");
        }
        return orderDAO.findOrderItems(orderId);
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) throws SQLException {
        if (orderId == null || newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID and new status must be provided.");
        }
        return orderDAO.updateOrderStatus(orderId, newStatus.trim().toLowerCase());
    }

    public boolean cancelOrder(Long orderId) throws SQLException {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }

        Order order = orderOpt.get();
        String currentStatus = order.getOrderStatus();
        if ("delivered".equalsIgnoreCase(currentStatus) || 
            "cancelled".equalsIgnoreCase(currentStatus) || 
            "refunded".equalsIgnoreCase(currentStatus)) {
            throw new IllegalStateException("Order in status '" + currentStatus + "' cannot be cancelled.");
        }

        boolean updated = orderDAO.updateOrderStatus(orderId, "cancelled");
        if (updated) {
            // Restore inventory for all items in the cancelled order
            List<OrderItem> items = orderDAO.findOrderItems(orderId);
            for (OrderItem item : items) {
                productDAO.updateStock(item.getProductId(), item.getQuantity());
            }
        }
        return updated;
    }
}

