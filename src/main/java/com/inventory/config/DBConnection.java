/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection koneksi;
    
    public static Connection getConnection() {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            
            String url = "jdbc:mysql://localhost:3306/inventory_db"; 
            
            koneksi = DriverManager.getConnection(url, "root", "");
            System.out.println("Berhasil Koneksi Ke Database inventory_db");
            
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver Tidak Ditemukan: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("Gagal Koneksi Ke Database: " + ex.getMessage());
        }
        return koneksi;
    }

    public static void main(String[] args) {
        
        getConnection();
    }
}
