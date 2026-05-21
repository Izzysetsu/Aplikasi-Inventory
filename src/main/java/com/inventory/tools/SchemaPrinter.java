package com.inventory.tools;

import com.inventory.config.DBConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaPrinter {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Failed to connect to database");
            return;
        }
        
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables("inventory_db", null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTABLE: " + tableName);
                System.out.println("----------------------------------------");
                
                ResultSet columns = metaData.getColumns("inventory_db", null, tableName, "%");
                while (columns.next()) {
                    String colName = columns.getString("COLUMN_NAME");
                    String type = columns.getString("TYPE_NAME");
                    int size = columns.getInt("COLUMN_SIZE");
                    System.out.println(String.format("  - %-20s : %-15s (%d)", colName, type, size));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
