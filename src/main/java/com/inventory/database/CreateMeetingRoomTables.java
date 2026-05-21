package com.inventory.database;

import com.inventory.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class CreateMeetingRoomTables {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Table meeting_rooms
            String createRooms = "CREATE TABLE IF NOT EXISTS meeting_rooms (" +
                    "room_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "capacity INT NOT NULL DEFAULT 0, " +
                    "is_deleted INT DEFAULT 0" +
                    ")";
            stmt.executeUpdate(createRooms);
            System.out.println("Tabel meeting_rooms berhasil dibuat/sudah ada.");

            // 2. Table meeting_bookings
            String createBookings = "CREATE TABLE IF NOT EXISTS meeting_bookings (" +
                    "booking_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "room_id INT NOT NULL, " +
                    "start_time DATETIME NOT NULL, " +
                    "end_time DATETIME NOT NULL, " +
                    "pic_name VARCHAR(100) NOT NULL, " +
                    "division VARCHAR(100), " +
                    "participants INT NOT NULL, " +
                    "purpose TEXT, " +
                    "status VARCHAR(50) DEFAULT 'Approved', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (room_id) REFERENCES meeting_rooms(room_id)" +
                    ")";
            stmt.executeUpdate(createBookings);
            System.out.println("Tabel meeting_bookings berhasil dibuat/sudah ada.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
