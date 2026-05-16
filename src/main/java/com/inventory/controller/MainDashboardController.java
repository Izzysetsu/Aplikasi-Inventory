package com.inventory.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainDashboardController {

    @FXML private StackPane contentArea;
    @FXML private VBox sidebar;
    @FXML private Label brandText, lblMenu, lblTrx;
    @FXML private Button btnCategory, btnSupplier, btnAsset, btnLoan;
    @FXML private Button btnReportLoan;

    private boolean isExpanded = true;

    @FXML
    private void toggleSidebar() {
        if (isExpanded) {
            // Sembunyikan Sidebar
            sidebar.setPrefWidth(60);
            brandText.setVisible(false);
            lblMenu.setVisible(false);
            lblTrx.setVisible(false);
            
            btnCategory.setText("📦");
            btnSupplier.setText("🚚");
            btnAsset.setText("🖥️");
            btnLoan.setText("🤝");
            isExpanded = false;
        } else {
            // Tampilkan Sidebar
            sidebar.setPrefWidth(250);
            brandText.setVisible(true);
            lblMenu.setVisible(true);
            lblTrx.setVisible(true);
            
            btnCategory.setText("📦  Data Kategori");
            btnSupplier.setText("🚚  Data Supplier");
            btnAsset.setText("🖥️  Data Asset");
            btnLoan.setText("🤝  Peminjaman");
            isExpanded = true;
        }
    }

    // Fungsi Pindah Halaman
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
    private void showLoan() { 
        loadPage("/fxml/TransactionLoan.fxml"); 
    }
    
    @FXML 
    private void showReportLoan() { 
        loadPage("/fxml/ReportLoan.fxml"); 
    }
    
    // Fungsi Utama untuk me-load file FXML ke dalam kotak tengah (StackPane)
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