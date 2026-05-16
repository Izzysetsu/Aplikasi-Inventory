package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private final Connection conn;

    public SupplierDAO() {
        this.conn = DBConnection.getConnection();
    }

    // 1. Simpan Data Baru
    public boolean insert(Supplier sup) {
        String sql = "INSERT INTO suppliers (name, phone, email, address, is_deleted) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sup.getName());
            ps.setString(2, sup.getPhone());
            ps.setString(3, sup.getEmail());
            ps.setString(4, sup.getAddress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Supplier: " + e.getMessage());
            return false;
        }
    }

    // 2. Soft Delete
    public boolean delete(int id) {
        String sql = "UPDATE suppliers SET is_deleted = 1 WHERE supplier_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Supplier: " + e.getMessage());
            return false;
        }
    }

    // 3. Tampil Semua Data
    public List<Supplier> getAll() {
        return search(""); // Pakai fungsi search dengan keyword kosong
    }

    // 4. Cari Berdasarkan Nama, Telepon, atau Email 
    public List<Supplier> search(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE is_deleted = 0 AND (name LIKE ? OR phone LIKE ? OR email LIKE ?) ORDER BY supplier_id DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier sup = new Supplier();
                sup.setId(rs.getInt("supplier_id"));
                sup.setName(rs.getString("name"));
                sup.setPhone(rs.getString("phone"));
                sup.setEmail(rs.getString("email"));
                sup.setAddress(rs.getString("address"));
                list.add(sup);
            }
        } catch (SQLException e) {
            System.err.println("Error Search Supplier: " + e.getMessage());
        }
        return list;
    }
    // 5. Update Data
    public boolean update(Supplier sup) {
        String sql = "UPDATE suppliers SET name=?, phone=?, email=?, address=? WHERE supplier_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sup.getName());
            ps.setString(2, sup.getPhone());
            ps.setString(3, sup.getEmail());
            ps.setString(4, sup.getAddress());
            ps.setInt(5, sup.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Supplier: " + e.getMessage());
            return false;
        }
    }
}