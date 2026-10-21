package com.shopsphere.dao;

import com.shopsphere.model.CartItem;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAO {

    private static final String SQL_UPSERT_ITEM = 
        "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

    private static final String SQL_FIND_BY_USER_ID = 
        "SELECT cart_id, user_id, product_id, quantity, created_at, updated_at " +
        "FROM cart WHERE user_id = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_BY_ID = 
        "SELECT cart_id, user_id, product_id, quantity, created_at, updated_at " +
        "FROM cart WHERE cart_id = ?";

    private static final String SQL_UPDATE_QUANTITY = 
        "UPDATE cart SET quantity = ? WHERE cart_id = ?";

    private static final String SQL_DELETE_ITEM = 
        "DELETE FROM cart WHERE cart_id = ?";

    private static final String SQL_CLEAR_CART = 
        "DELETE FROM cart WHERE user_id = ?";

    public boolean addOrUpdateItem(CartItem item) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPSERT_ITEM)) {

            ps.setLong(1, item.getUserId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity() != null ? item.getQuantity() : 1);

            return ps.executeUpdate() > 0;
        }
    }

    public Optional<CartItem> findById(Long cartId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCartItem(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<CartItem> findByUserId(Long userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToCartItem(rs));
                }
            }
        }
        return items;
    }

    public boolean updateQuantity(Long cartId, int newQuantity) throws SQLException {
        if (newQuantity <= 0) {
            return deleteItem(cartId);
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_QUANTITY)) {

            ps.setInt(1, newQuantity);
            ps.setLong(2, cartId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(Long cartId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ITEM)) {

            ps.setLong(1, cartId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean clearCart(Long userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_CART)) {

            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0;
        }
    }

    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        return new CartItem(
            rs.getLong("cart_id"),
            rs.getLong("user_id"),
            rs.getLong("product_id"),
            rs.getInt("quantity"),
            createdTs != null ? createdTs.toLocalDateTime() : null,
            updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }
}

