package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.AssetHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetHistoryDAO {
    private Connection conn;

    public AssetHistoryDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<AssetHistory> getAllHistory() {
        List<AssetHistory> list = new ArrayList<>();
        
        String sql = "SELECT DATE(a.created_at) as trans_date, a.barcode_code, a.name as asset_name, " +
                     "'Barang Masuk' as trans_type, a.quantity, l.name as location, 'Stok Awal / Penambahan' as purpose " +
                     "FROM assets a LEFT JOIN locations l ON a.location_id = l.location_id " +
                     "WHERE a.is_deleted = 0 " +
                     
                     "UNION ALL " +
                     
                     "SELECT o.outbound_date as trans_date, a.barcode_code, a.name as asset_name, " +
                     "'Keluar / Mutasi' as trans_type, o.quantity, l.name as location, o.purpose " +
                     "FROM outbounds o JOIN assets a ON o.asset_id = a.asset_id " +
                     "JOIN locations l ON o.location_id = l.location_id " +
                     
                     "UNION ALL " +
                     
                     "SELECT ln.loan_date as trans_date, a.barcode_code, a.name as asset_name, " +
                     "CONCAT('Dipinjam (', ln.status, ')') as trans_type, ln.quantity, ln.location_destination as location, " +
                     "CONCAT(ln.pic_name, ' (', ln.division, ') - ', IFNULL(ln.purpose, '')) as purpose " +
                     "FROM loans ln JOIN assets a ON ln.asset_id = a.asset_id " +
                     
                     "ORDER BY trans_date DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AssetHistory h = new AssetHistory();
                h.setTransDate(rs.getString("trans_date"));
                h.setBarcodeCode(rs.getString("barcode_code"));
                h.setAssetName(rs.getString("asset_name"));
                h.setTransType(rs.getString("trans_type"));
                h.setQuantity(rs.getInt("quantity"));
                h.setLocation(rs.getString("location"));
                h.setPurpose(rs.getString("purpose"));
                list.add(h);
            }
        } catch (SQLException e) {
            System.err.println("Error get Asset History: " + e.getMessage());
        }
        
        return list;
    }
}
