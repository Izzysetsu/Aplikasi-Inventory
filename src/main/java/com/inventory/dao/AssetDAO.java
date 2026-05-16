package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Asset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {
    private Connection conn;

    public AssetDAO() {
        this.conn = DBConnection.getConnection();
    }

    // 1. Tampil Semua Data
    public List<Asset> getAll() {
        List<Asset> list = new ArrayList<>();
    
        String sql = "SELECT a.*, c.name as category_name FROM assets a " +
                     "LEFT JOIN categories c ON a.category_id = c.category_id ORDER BY a.asset_id DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Asset a = new Asset();
                a.setId(rs.getInt("asset_id"));
                a.setBarcode(rs.getString("barcode_code"));
                a.setName(rs.getString("name"));
                a.setCategoryName(rs.getString("category_name"));
                a.setStatus(rs.getString("status"));
                a.setSpecification(rs.getString("specification"));
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. Simpan Data Baru
    public boolean insert(Asset a) {
        String sql = "INSERT INTO assets (barcode_code, name, category_id, specification, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getBarcode());
            ps.setString(2, a.getName());
            ps.setInt(3, a.getCategoryId());
            ps.setString(4, a.getSpecification());
            ps.setString(5, a.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }


    // 3. Update Data Asset
    public boolean update(Asset a) {
        String sql = "UPDATE assets SET barcode_code=?, name=?, category_id=?, specification=?, status=? WHERE asset_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getBarcode());
            ps.setString(2, a.getName());
            ps.setInt(3, a.getCategoryId());
            ps.setString(4, a.getSpecification());
            ps.setString(5, a.getStatus());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Asset: " + e.getMessage());
            return false;
        }
    }

    // 4. Hapus Data Asset (Hard Delete)
    public boolean delete(int id) {
        String sql = "DELETE FROM assets WHERE asset_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Asset: " + e.getMessage());
            return false;
        }
    }

    // 5. Fitur Pencarian Data Asset
    public List<Asset> search(String keyword) {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT a.*, c.name as category_name FROM assets a " +
                     "LEFT JOIN categories c ON a.category_id = c.category_id " +
                     "WHERE a.name LIKE ? OR a.barcode_code LIKE ? " +
                     "ORDER BY a.asset_id DESC";
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Asset a = new Asset();
                a.setId(rs.getInt("asset_id"));
                a.setBarcode(rs.getString("barcode_code"));
                a.setName(rs.getString("name"));
                a.setCategoryName(rs.getString("category_name"));
                a.setStatus(rs.getString("status"));
                a.setSpecification(rs.getString("specification"));
                list.add(a);
            }
        } catch (SQLException e) { 
            System.err.println("Error Search Asset: " + e.getMessage());
        }
        return list;
    }

} 