package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    
    private Connection conn;

    public LocationDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<Location> getAll() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM locations WHERE is_deleted = 0 ORDER BY location_id DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Location loc = new Location();
                loc.setId(rs.getInt("location_id"));
                loc.setName(rs.getString("name"));
                loc.setDescription(rs.getString("description"));
                list.add(loc);
            }
        } catch (SQLException e) {
            System.err.println("Error Get All Locations: " + e.getMessage());
        }
        return list;
    }

    // Tambah data lokasi baru
    public boolean insert(Location loc) {
        String sql = "INSERT INTO locations (name, description, is_deleted) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loc.getName());
            ps.setString(2, loc.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Location: " + e.getMessage());
            return false;
        }
    }

    // Update / Ubah data lokasi
    public boolean update(Location loc) {
        String sql = "UPDATE locations SET name = ?, description = ? WHERE location_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loc.getName());
            ps.setString(2, loc.getDescription());
            ps.setInt(3, loc.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Location: " + e.getMessage());
            return false;
        }
    }

    // Hapus data lokasi (Soft Delete )
    public boolean delete(int id) {
        String sql = "UPDATE locations SET is_deleted = 1 WHERE location_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Delete Location: " + e.getMessage());
            return false;
        }
    }
}