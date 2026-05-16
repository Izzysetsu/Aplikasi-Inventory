package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private Connection conn;

    public CategoryDAO() {
        this.conn = DBConnection.getConnection();
    }

    // 1. Ambil Semua Data
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_deleted = 0 ORDER BY category_id DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("category_id"));
                cat.setName(rs.getString("name"));
                cat.setStatus(rs.getString("status"));
                list.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("Error Get All Category: " + e.getMessage());
        }
        return list;
    }

    // 2. Tambah Data
    public boolean insert(Category cat) {
        String sql = "INSERT INTO categories (name, status) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Category: " + e.getMessage());
            return false;
        }
    }

    // 3. Update Data 
    public boolean update(Category cat) {
        String sql = "UPDATE categories SET name = ?, status = ? WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getStatus());
            ps.setInt(3, cat.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Category: " + e.getMessage());
            return false;
        }
    }

    // 4. Hapus Data (Soft Delete)
    public boolean delete(int id) {
        String sql = "UPDATE categories SET is_deleted = 1 WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Category: " + e.getMessage());
            return false;
        }
    }
}