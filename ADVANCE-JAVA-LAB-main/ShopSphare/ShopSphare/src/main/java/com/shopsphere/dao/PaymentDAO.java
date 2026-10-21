package com.shopsphere.dao;

import com.shopsphere.model.Payment;
import com.shopsphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class PaymentDAO {

    private static final String SQL_INSERT = 
        "INSERT INTO payments (order_id, payment_method, transaction_id, amount, payment_status, payment_gateway_response, paid_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID = 
        "SELECT payment_id, order_id, payment_method, transaction_id, amount, payment_status, payment_gateway_response, paid_at, created_at, updated_at " +
        "FROM payments WHERE payment_id = ?";

    private static final String SQL_FIND_BY_ORDER_ID = 
        "SELECT payment_id, order_id, payment_method, transaction_id, amount, payment_status, payment_gateway_response, paid_at, created_at, updated_at " +
        "FROM payments WHERE order_id = ?";

    private static final String SQL_UPDATE_STATUS = 
        "UPDATE payments SET payment_status = ?, transaction_id = ?, payment_gateway_response = ?, paid_at = ? " +
        "WHERE payment_id = ?";

    public Payment create(Payment payment) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, payment.getOrderId());
            ps.setString(2, payment.getPaymentMethod());
            ps.setString(3, payment.getTransactionId());
            ps.setBigDecimal(4, payment.getAmount());
            ps.setString(5, payment.getPaymentStatus() != null ? payment.getPaymentStatus() : "pending");
            ps.setString(6, payment.getPaymentGatewayResponse());

            if (payment.getPaidAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(payment.getPaidAt()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
            return payment;
        }
    }

    public Optional<Payment> findById(Long paymentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Payment> findByOrderId(Long orderId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ORDER_ID)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateStatus(Long paymentId, String status, String transactionId, String gatewayResponse, Timestamp paidAt) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {

            ps.setString(1, status);
            ps.setString(2, transactionId);
            ps.setString(3, gatewayResponse);
            ps.setTimestamp(4, paidAt);
            ps.setLong(5, paymentId);

            return ps.executeUpdate() > 0;
        }
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Timestamp paidTs = rs.getTimestamp("paid_at");
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        return new Payment(
            rs.getLong("payment_id"),
            rs.getLong("order_id"),
            rs.getString("payment_method"),
            rs.getString("transaction_id"),
            rs.getBigDecimal("amount"),
            rs.getString("payment_status"),
            rs.getString("payment_gateway_response"),
            paidTs != null ? paidTs.toLocalDateTime() : null,
            createdTs != null ? createdTs.toLocalDateTime() : null,
            updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }
}

