package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Asset;
import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {
    
    private Connection conn;

    public AssetDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Fungsi otomatis membuat kode aset baru (Contoh: AST-2026-001)
    public String generateAssetCode() {
        String prefix = "AST-" + Year.now().getValue() + "-";
        String sql = "SELECT barcode_code FROM assets WHERE barcode_code LIKE ? ORDER BY asset_id DESC LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String lastCode = rs.getString("barcode_code");
                int lastNumber = Integer.parseInt(lastCode.substring(lastCode.lastIndexOf("-") + 1));
                return prefix + String.format("%03d", lastNumber + 1);
            }
        } catch (SQLException e) {
            System.err.println("Error Generate Code: " + e.getMessage());
        }
        return prefix + "001";
    }

    public List<Asset> getAll() {
        return search("");
    }

    public List<Asset> search(String keyword) {
        List<Asset> list = new ArrayList<>();
        
        String sql = "SELECT a.*, c.name AS category_name, c.type AS category_type, s.name AS supplier_name " +
                     "FROM assets a " +
                     "LEFT JOIN categories c ON a.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON a.supplier_id = s.supplier_id " +
                     "WHERE a.is_deleted = 0 AND (a.name LIKE ? OR a.barcode_code LIKE ?) " +
                     "ORDER BY a.asset_id DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Asset asset = new Asset();
                asset.setId(rs.getInt("asset_id"));
                asset.setBarcodeCode(rs.getString("barcode_code"));
                asset.setName(rs.getString("name"));
                
                asset.setCategoryId(rs.getInt("category_id"));
                asset.setCategoryName(rs.getString("category_name") != null ? rs.getString("category_name") : "-");
                
                asset.setSupplierId(rs.getInt("supplier_id"));
                asset.setSupplierName(rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "-");
                
                asset.setSpecification(rs.getString("specification"));
                asset.setStatus(rs.getString("status"));
                asset.setQuantity(rs.getInt("quantity"));
                asset.setUnit(rs.getString("unit"));
                asset.setCategoryType(rs.getString("category_type"));
                asset.setCreatedAt(rs.getString("created_at"));
                
                list.add(asset);
            }
        } catch (SQLException e) {
            System.err.println("Error Search Asset: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Asset asset) {
        String sql = "INSERT INTO assets (barcode_code, name, category_id, supplier_id, specification, status, quantity, unit, created_at, is_deleted) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (asset.getBarcodeCode() != null && !asset.getBarcodeCode().trim().isEmpty()) {
                ps.setString(1, asset.getBarcodeCode());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setString(2, asset.getName());
            ps.setInt(3, asset.getCategoryId());
            
            if (asset.getSupplierId() > 0) {
                ps.setInt(4, asset.getSupplierId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setString(5, asset.getSpecification());
            ps.setString(6, asset.getStatus());
            ps.setInt(7, asset.getQuantity());
            ps.setString(8, asset.getUnit() != null ? asset.getUnit() : "pcs");
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Asset: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Asset asset) {
        String sql = "UPDATE assets SET name=?, category_id=?, supplier_id=?, specification=?, status=?, quantity=?, unit=? WHERE asset_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, asset.getName());
            ps.setInt(2, asset.getCategoryId());
            
            if (asset.getSupplierId() > 0) {
                ps.setInt(3, asset.getSupplierId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            ps.setString(4, asset.getSpecification());
            ps.setString(5, asset.getStatus());
            ps.setInt(6, asset.getQuantity());
            ps.setString(7, asset.getUnit() != null ? asset.getUnit() : "pcs");
            ps.setInt(8, asset.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Asset: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "UPDATE assets SET is_deleted = 1 WHERE asset_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Asset: " + e.getMessage());
            return false;
        }
    }

    public List<Asset> getAvailableForLoan() {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT a.*, c.name AS category_name, c.type AS category_type, s.name AS supplier_name " +
                     "FROM assets a " +
                     "LEFT JOIN categories c ON a.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON a.supplier_id = s.supplier_id " +
                     "WHERE a.is_deleted = 0 AND (a.status = 'Idle' OR a.quantity > 0) " +
                     "ORDER BY a.asset_id DESC";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Asset asset = new Asset();
                asset.setId(rs.getInt("asset_id"));
                asset.setBarcodeCode(rs.getString("barcode_code"));
                asset.setName(rs.getString("name"));
                
                asset.setCategoryId(rs.getInt("category_id"));
                asset.setCategoryName(rs.getString("category_name") != null ? rs.getString("category_name") : "-");
                
                asset.setSupplierId(rs.getInt("supplier_id"));
                asset.setSupplierName(rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "-");
                
                asset.setSpecification(rs.getString("specification"));
                asset.setStatus(rs.getString("status"));
                asset.setQuantity(rs.getInt("quantity"));
                asset.setUnit(rs.getString("unit"));
                asset.setCategoryType(rs.getString("category_type"));
                asset.setCreatedAt(rs.getString("created_at"));
                
                list.add(asset);
            }
        } catch (SQLException e) {
            System.err.println("Error Get Available Assets: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int id, String newStatus) {
        String sql = "UPDATE assets SET status = ? WHERE asset_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Asset Status: " + e.getMessage());
            return false;
        }
    }

    public boolean deductQuantity(int id, int qtyToDeduct) {
        String sql = "UPDATE assets SET quantity = quantity - ? WHERE asset_id = ? AND quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qtyToDeduct);
            ps.setInt(2, id);
            ps.setInt(3, qtyToDeduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Deduct Asset Quantity: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateQuantity(int id, int newQty) {
        String sql = "UPDATE assets SET quantity = ? WHERE asset_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Quantity: " + e.getMessage());
            return false;
        }
    }
}