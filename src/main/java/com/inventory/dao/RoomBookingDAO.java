package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.RoomBooking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomBookingDAO {
    private Connection conn;

    public RoomBookingDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<RoomBooking> getAll() {
        return getByStatus(null);
    }

    public List<RoomBooking> getByStatus(String status) {
        List<RoomBooking> list = new ArrayList<>();
        String sql = "SELECT b.*, r.name as room_name " +
                     "FROM meeting_bookings b " +
                     "JOIN meeting_rooms r ON b.room_id = r.room_id ";
        
        if (status != null && !status.isEmpty()) {
            sql += "WHERE b.status = ? ";
        }
        sql += "ORDER BY b.start_time DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null && !status.isEmpty()) {
                ps.setString(1, status);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomBooking b = new RoomBooking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setRoomId(rs.getInt("room_id"));
                b.setRoomName(rs.getString("room_name"));
                b.setStartTime(rs.getString("start_time"));
                b.setEndTime(rs.getString("end_time"));
                b.setPicName(rs.getString("pic_name"));
                b.setDivision(rs.getString("division"));
                b.setPosition(rs.getString("position"));
                b.setParticipants(rs.getInt("participants"));
                b.setPurpose(rs.getString("purpose"));
                b.setStatus(rs.getString("status"));
                b.setCreatedAt(rs.getString("created_at"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error get Bookings: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(RoomBooking booking) {
        String sql = "INSERT INTO meeting_bookings (room_id, start_time, end_time, pic_name, division, position, participants, purpose, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getRoomId());
            ps.setString(2, booking.getStartTime());
            ps.setString(3, booking.getEndTime());
            ps.setString(4, booking.getPicName());
            ps.setString(5, booking.getDivision());
            ps.setString(6, booking.getPosition());
            ps.setInt(7, booking.getParticipants());
            ps.setString(8, booking.getPurpose());
            ps.setString(9, booking.getStatus() != null ? booking.getStatus() : "Approved");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insert Booking: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int bookingId, String newStatus) {
        String sql = "UPDATE meeting_bookings SET status=? WHERE booking_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error update Booking Status: " + e.getMessage());
            return false;
        }
    }
}
