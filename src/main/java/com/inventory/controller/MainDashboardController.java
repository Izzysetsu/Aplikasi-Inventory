package com.inventory.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class MainDashboardController {

    @FXML private StackPane contentArea;
    @FXML private VBox sidebar;
    @FXML private VBox userSubMenu, peminjamanSubMenu, meetingSubMenu;
    @FXML private Label brandText, lblInventory, lblMaster;
    @FXML private Button btnCategory, btnSupplier, btnAsset, btnStockOpname, btnLocation, btnStatus, btnLoan, btnReportAsset, btnReportLoan;
    @FXML private Button btnPeminjamanManage, btnReqBarang, btnListPeminjaman;
    @FXML private Button btnPermission, btnUserManage, btnListUser, btnRegisterUser, btnLogUser, btnChangePassword, btnLogout;

    private boolean isExpanded = true;

    @FXML
    private void toggleSidebar() {
        if (isExpanded) {
            // Sembunyikan Sidebar 
            sidebar.setPrefWidth(60);
            brandText.setVisible(false);
            if(lblInventory != null) lblInventory.setVisible(false);
            if(lblMaster != null) lblMaster.setVisible(false);
            
            btnCategory.setText("KTG");
            btnSupplier.setText("SPL");
            btnAsset.setText("IN");
            if(btnStockOpname != null) btnStockOpname.setText("STK");
            btnLoan.setText("OUT");
            if(btnReportAsset != null) btnReportAsset.setText("RPT");
            if(btnPeminjamanManage != null) btnPeminjamanManage.setText("PNJ " + (peminjamanSubMenu != null && peminjamanSubMenu.isVisible() ? "▲" : "▼"));
            if(btnReqBarang != null) btnReqBarang.setText("REQ");
            if(btnListPeminjaman != null) btnListPeminjaman.setText("LST");
            btnReportLoan.setText("RLN");
            if(btnLocation != null) btnLocation.setText("LKS");
            if(btnPermission != null) btnPermission.setText("PRM");
            if(btnUserManage != null) btnUserManage.setText("USR " + (userSubMenu.isVisible() ? "▲" : "▼"));
            if(btnListUser != null) btnListUser.setText("LST");
            if(btnRegisterUser != null) btnRegisterUser.setText("REG");
            if(btnLogUser != null) btnLogUser.setText("LOG");
            if(btnChangePassword != null) btnChangePassword.setText("PWD");
            if(btnLogout != null) btnLogout.setText("OUT");
            isExpanded = false;
        } else {
            // Tampilkan Sidebar 
            sidebar.setPrefWidth(250);
            brandText.setVisible(true);
            if(lblInventory != null) lblInventory.setVisible(true);
            if(lblMaster != null) lblMaster.setVisible(true);
            
            btnCategory.setText("📦  Data Kategori");
            btnSupplier.setText("🚚  Data Supplier");
            btnAsset.setText("📦  Data Barang Masuk");
            if(btnStockOpname != null) btnStockOpname.setText("📋  Stock Opname");
            btnLoan.setText("📤  Data Barang Keluar");
            if(btnReportAsset != null) btnReportAsset.setText("📊  Laporan Barang");
            if(btnPeminjamanManage != null) btnPeminjamanManage.setText("🤝  Peminjaman " + (peminjamanSubMenu != null && peminjamanSubMenu.isVisible() ? "▲" : "▼"));
            if(btnReqBarang != null) btnReqBarang.setText("📝  Request Barang");
            if(btnListPeminjaman != null) btnListPeminjaman.setText("📋  List Peminjaman");
            btnReportLoan.setText("📊  Laporan Peminjaman");
            if(btnLocation != null) btnLocation.setText("🏢  Data Lokasi");
            if(btnPermission != null) btnPermission.setText("🔐  Permission");
            if(btnUserManage != null) btnUserManage.setText("👥  Manajemen User " + (userSubMenu.isVisible() ? "▲" : "▼"));
            if(btnListUser != null) btnListUser.setText("📝  List User");
            if(btnRegisterUser != null) btnRegisterUser.setText("➕  Register User");
            if(btnLogUser != null) btnLogUser.setText("🕒  Log User");
            if(btnChangePassword != null) btnChangePassword.setText("🔑  Ubah Password");
            if(btnLogout != null) btnLogout.setText("🚪  Logout");
            isExpanded = true;
        }
    }

    @FXML 
    private void showCategory() { 
        loadPage("/fxml/MasterCategory.fxml"); 
    }
    
    @FXML 
    private void showSupplier() { 
        loadPage("/fxml/MasterSupplier.fxml"); 
    }
    
    @FXML 
    private void showAsset() { 
        loadPage("/fxml/MasterAsset.fxml"); 
    }
    
    @FXML 
    private void showLocation() {
        loadPage("/fxml/MasterLocation.fxml"); 
    }
    
    @FXML 
    private void showStatus() {
        loadPage("/fxml/MasterStatus.fxml"); 
    }
    
    @FXML 
    private void showLoan() { 
        loadPage("/fxml/TransactionOutbound.fxml"); 
    }
    
    @FXML 
    private void showReportLoan() { 
        loadPage("/fxml/ReportLoan.fxml"); 
    }
    
    @FXML 
    private void showReportAsset() { 
        loadPage("/fxml/ReportAsset.fxml"); 
    }
    
    @FXML 
    private void showStockOpname() { 
        loadPage("/fxml/StockOpname.fxml"); 
    }
    
    @FXML 
    private void toggleUserMenu() {
        if (userSubMenu != null) {
            boolean isVisible = userSubMenu.isVisible();
            userSubMenu.setVisible(!isVisible);
            userSubMenu.setManaged(!isVisible);
            if (isExpanded) {
                btnUserManage.setText("👥  Manajemen User " + (!isVisible ? "▲" : "▼"));
            } else {
                btnUserManage.setText("USR " + (!isVisible ? "▲" : "▼"));
            }
        }
    }
    
    @FXML 
    private void togglePeminjamanMenu() {
        if (peminjamanSubMenu != null) {
            boolean isVisible = peminjamanSubMenu.isVisible();
            peminjamanSubMenu.setVisible(!isVisible);
            peminjamanSubMenu.setManaged(!isVisible);
            if (isExpanded) {
                btnPeminjamanManage.setText("🤝  Peminjaman " + (!isVisible ? "▲" : "▼"));
            } else {
                btnPeminjamanManage.setText("PNJ " + (!isVisible ? "▲" : "▼"));
            }
        }
    }

    @FXML
    private void toggleMeetingMenu() {
        if (meetingSubMenu != null) {
            boolean isVisible = meetingSubMenu.isVisible();
            meetingSubMenu.setVisible(!isVisible);
            meetingSubMenu.setManaged(!isVisible);
        }
    }
    
    @FXML private void showReqBarang() { loadPage("/fxml/RequestBarang.fxml"); }
    @FXML private void showListPeminjaman() { loadPage("/fxml/ListPeminjaman.fxml"); }

    @FXML private void showMasterRoom() { loadPage("/fxml/MasterRoom.fxml"); }
    @FXML private void showRequestRoom() { loadPage("/fxml/RequestRoom.fxml"); }
    @FXML private void showListRoomBooking() { loadPage("/fxml/ListRoomBooking.fxml"); }
    @FXML private void showReportRoomBooking() { loadPage("/fxml/ReportRoomBooking.fxml"); }
    
    @FXML private void showPermission() { loadBlankPage("Permission"); }
    @FXML private void showListUser() { loadBlankPage("List User"); }
    @FXML private void showRegisterUser() { loadBlankPage("Register User"); }
    @FXML private void showLogUser() { loadBlankPage("Log User"); }
    @FXML private void showChangePassword() { loadBlankPage("Ubah Password"); }

    private void loadBlankPage(String featureName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BlankPage.fxml"));
            Parent root = loader.load();
            BlankPageController controller = loader.getController();
            controller.setFeatureName(featureName);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman Blank Page untuk " + featureName);
            e.printStackTrace();
        }
    }

    // ================= LOGOUT =================
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin keluar dari sistem?");

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage dashboardStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                dashboardStage.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                
                Stage loginStage = new Stage();
                loginStage.setTitle("Login - Inventory IJI");
                loginStage.setScene(new Scene(root));
                loginStage.centerOnScreen();
                loginStage.setResizable(false);
                loginStage.show();
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman Login.");
            }
        }
    }
    // =========================================================
    
    private void loadPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }
}