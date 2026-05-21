package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Status;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusDAO {
    private Connection conn;

    public StatusDAO() {
        this.conn = DBConnection.getConnection();
    }

    // 1. Ambil Semua Data 
    public List<Status> getAll() {
        List<Status> list = new ArrayList<>();
        String sql = "SELECT * FROM statuses WHERE is_deleted = 0 ORDER BY status_id ASC";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Status st = new Status();
                st.setId(rs.getInt("status_id"));
                st.setName(rs.getString("name"));
                st.setDescription(rs.getString("description"));
                list.add(st);
            }
        } catch (SQLException e) {
            System.err.println("Error Get All Status: " + e.getMessage());
        }
        return list;
    }

    // 2. Tambah Data
    public boolean insert(Status st) {
        String sql = "INSERT INTO statuses (name, description, is_deleted) VALUES (?, ?, 0)"; 
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, st.getName());
            ps.setString(2, st.getDescription() != null ? st.getDescription() : ""); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Status: " + e.getMessage());
            return false;
        }
    }

    // 3. Update Data 
    public boolean update(Status st) {
        String sql = "UPDATE statuses SET name = ?, description = ? WHERE status_id = ?"; 
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, st.getName());
            ps.setString(2, st.getDescription() != null ? st.getDescription() : ""); 
            ps.setInt(3, st.getId()); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Status: " + e.getMessage());
            return false;
        }
    }

    // 4. Hapus Data (Soft Delete)
    public boolean delete(int id) {
        String sql = "UPDATE statuses SET is_deleted = 1 WHERE status_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Status: " + e.getMessage());
            return false;
        }
    }
}
