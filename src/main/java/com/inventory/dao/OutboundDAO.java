package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Outbound;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OutboundDAO {
    private Connection conn;

    public OutboundDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<Outbound> getAll() {
        return search("");
    }

    public List<Outbound> search(String keyword) {
        List<Outbound> list = new ArrayList<>();
        String sql = "SELECT o.*, a.name AS asset_name, a.barcode_code, l.name AS location_name " +
                     "FROM outbounds o " +
                     "JOIN assets a ON o.asset_id = a.asset_id " +
                     "JOIN locations l ON o.location_id = l.location_id " +
                     "WHERE a.name LIKE ? OR a.barcode_code LIKE ? OR l.name LIKE ? " +
                     "ORDER BY o.outbound_date DESC, o.outbound_id DESC";
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Outbound out = new Outbound();
                out.setOutboundId(rs.getInt("outbound_id"));
                out.setAssetId(rs.getInt("asset_id"));
                out.setAssetName(rs.getString("asset_name"));
                out.setBarcodeCode(rs.getString("barcode_code"));
                out.setLocationId(rs.getInt("location_id"));
                out.setLocationName(rs.getString("location_name"));
                out.setQuantity(rs.getInt("quantity"));
                out.setOutboundDate(rs.getString("outbound_date"));
                out.setPurpose(rs.getString("purpose"));
                
                list.add(out);
            }
        } catch (SQLException e) {
            System.err.println("Error Search Outbound: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Outbound outbound) {
        String sql = "INSERT INTO outbounds (asset_id, location_id, quantity, outbound_date, purpose) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, outbound.getAssetId());
            ps.setInt(2, outbound.getLocationId());
            ps.setInt(3, outbound.getQuantity());
            ps.setString(4, outbound.getOutboundDate());
            ps.setString(5, outbound.getPurpose() != null ? outbound.getPurpose() : "");
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Outbound: " + e.getMessage());
            return false;
        }
    }
}
