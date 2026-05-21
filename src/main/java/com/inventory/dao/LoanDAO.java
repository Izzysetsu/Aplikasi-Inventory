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
        String sql = "INSERT INTO loans (pic_name, division, asset_id, location_destination, loan_date, return_date_estimate, purpose, status, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loan.getPicName());
            ps.setString(2, loan.getDivision());
            ps.setInt(3, loan.getAssetId());
            ps.setString(4, loan.getLocation());
            ps.setString(5, loan.getLoanDate());
            ps.setString(6, loan.getReturnDate());
            ps.setString(7, loan.getPurpose());
            ps.setString(8, loan.getStatus());
            ps.setInt(9, loan.getQuantity());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Insert Peminjaman: " + e.getMessage());
            return false;
        }
    }

    // Fungsi Get All 
    public List<Loan> getAllLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT l.*, a.barcode_code, a.name AS asset_name, c.type AS category_type FROM loans l " +
                     "JOIN assets a ON l.asset_id = a.asset_id " +
                     "JOIN categories c ON a.category_id = c.category_id ORDER BY l.loan_id DESC";
        
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
                loan.setPurpose(rs.getString("purpose"));
                loan.setStatus(rs.getString("status"));
                loan.setQuantity(rs.getInt("quantity"));
                loan.setCategoryType(rs.getString("category_type"));
                list.add(loan);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Fungsi Update Pengembalian
    public boolean updateReturnDateAndStatus(int loanId, String returnDate, String status) {
        String sql = "UPDATE loans SET return_date_estimate = ?, status = ? WHERE loan_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setString(2, status);
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error Update Return: " + e.getMessage());
            return false;
        }
    }
    
    // Fungsi Get by Status
    public List<Loan> getLoansByStatus(String status) {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT l.*, a.barcode_code, a.name AS asset_name, a.asset_id AS actual_asset_id FROM loans l " +
                     "JOIN assets a ON l.asset_id = a.asset_id WHERE l.status = ? ORDER BY l.loan_id DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan();
                    loan.setId(rs.getInt("loan_id"));
                    loan.setPicName(rs.getString("pic_name"));
                    loan.setDivision(rs.getString("division"));
                    loan.setAssetId(rs.getInt("actual_asset_id")); // We need this to update the asset later
                    
                    String barcode = rs.getString("barcode_code");
                    String prefix = (barcode != null && !barcode.isEmpty()) ? "[" + barcode + "] " : "";
                    loan.setAssetDisplay(prefix + rs.getString("asset_name"));
                    
                    loan.setLocation(rs.getString("location_destination"));
                    loan.setLoanDate(rs.getString("loan_date"));
                    loan.setReturnDate(rs.getString("return_date_estimate"));
                    loan.setPurpose(rs.getString("purpose"));
                    loan.setStatus(rs.getString("status"));
                    loan.setQuantity(rs.getInt("quantity"));
                    list.add(loan);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}