package com.inventory.database;

import com.inventory.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class AlterMeetingRoomTables {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            try {
                stmt.executeUpdate("ALTER TABLE meeting_rooms ADD COLUMN description VARCHAR(255)");
            } catch (Exception e) {}
            
            try {
                stmt.executeUpdate("ALTER TABLE meeting_rooms DROP COLUMN capacity");
            } catch (Exception e) {}

            try {
                stmt.executeUpdate("ALTER TABLE meeting_bookings ADD COLUMN position VARCHAR(100) AFTER division");
            } catch (Exception e) {}

            System.out.println("Tabel berhasil di-alter.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
