package com.inventory.tools;

import com.inventory.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class CreateOutboundTable {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Gagal koneksi ke database.");
            return;
        }
        
        String sql = "CREATE TABLE IF NOT EXISTS outbounds (" +
                     "outbound_id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "asset_id INT NOT NULL, " +
                     "location_id INT NOT NULL, " +
                     "quantity INT NOT NULL, " +
                     "outbound_date DATE NOT NULL, " +
                     "purpose TEXT, " +
                     "FOREIGN KEY (asset_id) REFERENCES assets(asset_id), " +
                     "FOREIGN KEY (location_id) REFERENCES locations(location_id)" +
                     ")";
                     
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel outbounds berhasil dibuat/sudah ada!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
