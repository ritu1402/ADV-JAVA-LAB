package com.shopsphere.dao;

import com.shopsphere.model.Coupon;
import com.shopsphere.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class CouponDAO {

    private static final String SQL_FIND_BY_CODE = 
        "SELECT coupon_id, code, discount_type, discount_value, min_order_amount, " +
        "max_discount_amount, usage_limit, usage_per_user, start_date, end_date, is_active, created_at, updated_at " +
        "FROM coupons WHERE code = ?";

    private static final String SQL_FIND_BY_ID = 
        "SELECT coupon_id, code, discount_type, discount_value, min_order_amount, " +
        "max_discount_amount, usage_limit, usage_per_user, start_date, end_date, is_active, created_at, updated_at " +
        "FROM coupons WHERE coupon_id = ?";

    private static final String SQL_GET_TOTAL_USAGE = 
        "SELECT COUNT(*) FROM coupon_usage WHERE coupon_id = ?";

    private static final String SQL_GET_USER_USAGE = 
        "SELECT COUNT(*) FROM coupon_usage WHERE coupon_id = ? AND user_id = ?";

    private static final String SQL_RECORD_USAGE = 
        "INSERT INTO coupon_usage (coupon_id, user_id, order_id, discount_applied, used_at) " +
        "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

    public Optional<Coupon> findByCode(String code) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoupon(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Coupon> findById(Integer couponId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoupon(rs));
                }
            }
        }
        return Optional.empty();
    }

    public int getTotalUsageCount(Integer couponId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_TOTAL_USAGE)) {

            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int getUserUsageCount(Integer couponId, Long userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_USER_USAGE)) {

            ps.setInt(1, couponId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean recordUsage(Integer couponId, Long userId, Long orderId, BigDecimal discountApplied) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_RECORD_USAGE)) {

            ps.setInt(1, couponId);
            ps.setLong(2, userId);
            ps.setLong(3, orderId);
            ps.setBigDecimal(4, discountApplied);

            return ps.executeUpdate() > 0;
        }
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Timestamp startTs = rs.getTimestamp("start_date");
        Timestamp endTs = rs.getTimestamp("end_date");
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        int usageLimitVal = rs.getInt("usage_limit");
        Integer usageLimit = rs.wasNull() ? null : usageLimitVal;

        return new Coupon(
            rs.getInt("coupon_id"),
            rs.getString("code"),
            rs.getString("discount_type"),
            rs.getBigDecimal("discount_value"),
            rs.getBigDecimal("min_order_amount"),
            rs.getBigDecimal("max_discount_amount"),
            usageLimit,
            rs.getInt("usage_per_user"),
            startTs != null ? startTs.toLocalDateTime() : null,
            endTs != null ? endTs.toLocalDateTime() : null,
            rs.getBoolean("is_active"),
            createdTs != null ? createdTs.toLocalDateTime() : null,
            updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }
}

