package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private Connection conn;

    public RoomDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM meeting_rooms WHERE is_deleted = 0 ORDER BY name ASC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setName(rs.getString("name"));
                room.setDescription(rs.getString("description"));
                room.setIsDeleted(rs.getInt("is_deleted"));
                list.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error get Rooms: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Room room) {
        String sql = "INSERT INTO meeting_rooms (name, description, is_deleted) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getName());
            ps.setString(2, room.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Room: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Room room) {
        String sql = "UPDATE meeting_rooms SET name=?, description=? WHERE room_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getName());
            ps.setString(2, room.getDescription());
            ps.setInt(3, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Room: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "UPDATE meeting_rooms SET is_deleted=1 WHERE room_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error delete Room: " + e.getMessage());
            return false;
        }
    }
}
