package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.LoanDAO;
import com.inventory.model.Asset;
import com.inventory.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RequestBarangController {

    @FXML private TextField txtPicName, txtDivision, txtPurpose, txtSelectedAsset, txtQty, txtSearch;
    @FXML private DatePicker dpLoanDate;
    @FXML private TableView<Asset> tableAsset;
    @FXML private TableColumn<Asset, String> colBarcode, colName, colCategory, colStatus;
    @FXML private TableColumn<Asset, Integer> colQty;
    @FXML private Button btnSubmit;

    private AssetDAO assetDao = new AssetDAO();
    private LoanDAO loanDao = new LoanDAO();
    private Asset selectedAsset = null;

    @FXML
    public void initialize() {
        tableAsset.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcodeCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        tableAsset.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedAsset = newVal;
                txtSelectedAsset.setText(newVal.getName());
                
                if ("ASET".equalsIgnoreCase(newVal.getCategoryType())) {
                    txtQty.setText("1");
                    txtQty.setDisable(true);
                } else {
                    txtQty.clear();
                    txtQty.setDisable(false);
                    txtQty.setPromptText("Max: " + newVal.getQuantity());
                }
                btnSubmit.setDisable(false);
            } else {
                clearSelection();
            }
        });

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

        dpLoanDate.setValue(LocalDate.now());
        loadData();
    }

    private void loadData() {
        // Hanya ambil yang idle/baik/tersedia dan qty > 0
        List<Asset> availableAssets = assetDao.getAll().stream()
            .filter(a -> a.getQuantity() > 0 && 
                         ("Idle".equalsIgnoreCase(a.getStatus()) || "Tersedia".equalsIgnoreCase(a.getStatus()) || "Baik".equalsIgnoreCase(a.getStatus())))
            .collect(Collectors.toList());
            
        tableAsset.getItems().setAll(availableAssets);
        clearSelection();
    }
    
    private void filterData(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadData();
            return;
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Asset> filtered = assetDao.getAll().stream()
            .filter(a -> a.getQuantity() > 0 && 
                         ("Idle".equalsIgnoreCase(a.getStatus()) || "Tersedia".equalsIgnoreCase(a.getStatus()) || "Baik".equalsIgnoreCase(a.getStatus())))
            .filter(a -> (a.getName() != null && a.getName().toLowerCase().contains(lowerCaseKeyword)) || 
                         (a.getBarcodeCode() != null && a.getBarcodeCode().toLowerCase().contains(lowerCaseKeyword)))
            .collect(Collectors.toList());
        tableAsset.getItems().setAll(filtered);
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadData();
    }

    private void clearSelection() {
        selectedAsset = null;
        txtSelectedAsset.clear();
        txtQty.clear();
        txtQty.setDisable(true);
        btnSubmit.setDisable(true);
    }

    @FXML
    private void handleSubmit() {
        if (txtPicName.getText().trim().isEmpty() || txtDivision.getText().trim().isEmpty() || 
            txtPurpose.getText().trim().isEmpty() || dpLoanDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Semua field formulir (Nama, Divisi, Keperluan, Tanggal) wajib diisi!");
            return;
        }

        if (selectedAsset == null) {
            showAlert(Alert.AlertType.WARNING, "Silakan pilih barang dari tabel!");
            return;
        }

        int qtyReq = 0;
        try {
            qtyReq = Integer.parseInt(txtQty.getText());
            if (qtyReq <= 0) throw new Exception();
        } catch(Exception e) {
            showAlert(Alert.AlertType.WARNING, "Jumlah (Qty) harus berupa angka valid lebih dari 0!");
            return;
        }

        if (qtyReq > selectedAsset.getQuantity()) {
            showAlert(Alert.AlertType.WARNING, "Stok tidak mencukupi! Sisa stok hanya " + selectedAsset.getQuantity());
            return;
        }

        Loan loan = new Loan();
        loan.setPicName(txtPicName.getText());
        loan.setDivision(txtDivision.getText());
        loan.setAssetId(selectedAsset.getId());
        loan.setLocation(txtDivision.getText()); // Lokasi default adalah divisi peminjam
        loan.setLoanDate(dpLoanDate.getValue().toString());
        loan.setPurpose(txtPurpose.getText());
        loan.setQuantity(qtyReq);

        // LOGIKA CERDAS: Pemisahan ASET vs ATK
        if ("ASET".equalsIgnoreCase(selectedAsset.getCategoryType())) {
            loan.setStatus("Dipinjam");
        } else {
            loan.setStatus("Selesai"); // ATK langsung habis, tidak perlu dikembalikan
        }

        if (loanDao.insert(loan)) {
            if ("ASET".equalsIgnoreCase(selectedAsset.getCategoryType())) {
                assetDao.updateStatus(loan.getAssetId(), "Dipinjam");
            } else {
                assetDao.deductQuantity(loan.getAssetId(), loan.getQuantity());
                if (selectedAsset.getQuantity() - loan.getQuantity() == 0) {
                    assetDao.updateStatus(loan.getAssetId(), "Habis");
                }
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Request barang berhasil diproses!");
            handleClearForm();
            loadData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal memproses request barang!");
        }
    }
    
    private void handleClearForm() {
        txtPicName.clear();
        txtDivision.clear();
        txtPurpose.clear();
        dpLoanDate.setValue(LocalDate.now());
        clearSelection();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }
}
