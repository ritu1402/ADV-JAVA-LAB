package com.shopsphere.dao;

import com.shopsphere.model.User;
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

public class UserDAO {

    private static final String SQL_INSERT = 
        "INSERT INTO users (first_name, last_name, email, password_hash, phone_number, role, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID = 
        "SELECT user_id, first_name, last_name, email, password_hash, phone_number, role, status, created_at, updated_at " +
        "FROM users WHERE user_id = ?";

    private static final String SQL_FIND_BY_EMAIL = 
        "SELECT user_id, first_name, last_name, email, password_hash, phone_number, role, status, created_at, updated_at " +
        "FROM users WHERE email = ?";

    private static final String SQL_UPDATE = 
        "UPDATE users SET first_name = ?, last_name = ?, phone_number = ?, role = ?, status = ? " +
        "WHERE user_id = ?";

    private static final String SQL_UPDATE_PASSWORD = 
        "UPDATE users SET password_hash = ? WHERE user_id = ?";

    private static final String SQL_DELETE = 
        "DELETE FROM users WHERE user_id = ?";

    private static final String SQL_FIND_ALL = 
        "SELECT user_id, first_name, last_name, email, password_hash, phone_number, role, status, created_at, updated_at " +
        "FROM users ORDER BY created_at DESC";

    public User create(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getRole() != null ? user.getRole() : "customer");
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "active");

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            return user;
        }
    }

    public Optional<User> findById(Long userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean update(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus());
            ps.setLong(6, user.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(Long userId, String newPasswordHash) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {

            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Long userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        return new User(
            rs.getLong("user_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("phone_number"),
            rs.getString("role"),
            rs.getString("status"),
            createdTs != null ? createdTs.toLocalDateTime() : null,
            updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }
}

