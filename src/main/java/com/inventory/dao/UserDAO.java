package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Fungsi untuk mengecek Login
    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            System.err.println("Error Login: " + e.getMessage());
        }
        
        // Jika return null, berarti login gagal (username/password salah)
        // Jika return object User, berarti login sukses
        return user; 
    }
}