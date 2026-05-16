package com.inventory.dao;

import com.inventory.config.DBConnection;
import com.inventory.model.Loan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private Connection conn;

    public LoanDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Fungsi Insert 
    public boolean insert(Loan loan) {
        String sql = "INSERT INTO loans (pic_name, division, asset_id, location_destination, loan_date, return_date_estimate, purpose, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Dipinjam')";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loan.getPicName());
            ps.setString(2, loan.getDivision());
            ps.setInt(3, loan.getAssetId());
            ps.setString(4, loan.getLocation());
            ps.setString(5, loan.getLoanDate());
            ps.setString(6, loan.getReturnDate());
            ps.setString(7, loan.getPurpose());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Peminjaman: " + e.getMessage());
            return false;
        }
    }

    // Fungsi Get All 
    public List<Loan> getAllLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT l.*, a.barcode_code, a.name AS asset_name FROM loans l " +
                     "JOIN assets a ON l.asset_id = a.asset_id ORDER BY l.loan_id DESC";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("loan_id"));
                loan.setPicName(rs.getString("pic_name"));
                loan.setDivision(rs.getString("division"));
                loan.setAssetDisplay("[" + rs.getString("barcode_code") + "] " + rs.getString("asset_name"));
                loan.setLocation(rs.getString("location_destination"));
                loan.setLoanDate(rs.getString("loan_date"));
                loan.setReturnDate(rs.getString("return_date_estimate"));
                loan.setStatus(rs.getString("status"));
                list.add(loan);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}