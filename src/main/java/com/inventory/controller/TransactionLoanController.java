package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.LoanDAO;
import com.inventory.model.Asset;
import com.inventory.model.Loan;
import com.inventory.dao.StatusDAO;
import com.inventory.dao.LocationDAO;
import com.inventory.model.Location;
import com.inventory.model.Status;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class TransactionLoanController {

    @FXML private TextField txtPicName;
    @FXML private TextField txtDivision;
    @FXML private ComboBox<Asset> cbAsset;
    @FXML private TextField txtQty;
    @FXML private ComboBox<String> cbLocation;
    @FXML private DatePicker dpLoanDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextArea txtPurpose;

    private AssetDAO assetDao = new AssetDAO();

    @FXML
    public void initialize() {
        loadAssets();
        loadStatuses();
        loadLocations();
        
        cbAsset.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("ASET".equalsIgnoreCase(newVal.getCategoryType())) {
                    txtQty.setText("1");
                    txtQty.setDisable(true);
                } else {
                    txtQty.clear();
                    txtQty.setDisable(false);
                    txtQty.setPromptText("Max: " + newVal.getQuantity() + " " + (newVal.getUnit() != null ? newVal.getUnit() : ""));
                }
            }
        });
    }

    private void loadLocations() {
        LocationDAO locDao = new LocationDAO();
        List<String> locNames = new ArrayList<>();
        for (Location loc : locDao.getAll()) {
            locNames.add(loc.getName());
        }
        cbLocation.setItems(FXCollections.observableArrayList(locNames));
    }

    private void loadStatuses() {
        StatusDAO statusDao = new StatusDAO();
        List<String> statusNames = new ArrayList<>();
        for (Status st : statusDao.getAll()) {
            statusNames.add(st.getName());
        }
        cbStatus.setItems(FXCollections.observableArrayList(statusNames));
    }

    private void loadAssets() {
        List<Asset> assets = assetDao.getAvailableForLoan();
        cbAsset.setItems(FXCollections.observableArrayList(assets));
        
        cbAsset.setCellFactory(lv -> new ListCell<Asset>() {
            @Override protected void updateItem(Asset item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String prefix = item.getBarcodeCode() != null && !item.getBarcodeCode().isEmpty() ? "[" + item.getBarcodeCode() + "] " : "[ATK] ";
                    setText(prefix + item.getName() + " (Stok: " + item.getQuantity() + ")");
                }
            }
        });
        
        cbAsset.setButtonCell(new ListCell<Asset>() {
            @Override protected void updateItem(Asset item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String prefix = item.getBarcodeCode() != null && !item.getBarcodeCode().isEmpty() ? "[" + item.getBarcodeCode() + "] " : "[ATK] ";
                    setText(prefix + item.getName() + " (Stok: " + item.getQuantity() + ")");
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        // --- PROSES VALIDASI ---
        
        if (txtPicName.getText() == null || txtPicName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama Peminjam (PIC) wajib diisi!");
            alert.showAndWait();
            txtPicName.requestFocus();
            return;
        }
        
        if (txtDivision.getText() == null || txtDivision.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Divisi wajib diisi!");
            alert.showAndWait();
            txtDivision.requestFocus();
            return;
        }
        
        if (cbAsset.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih Asset yang akan dipinjam!");
            alert.showAndWait();
            cbAsset.requestFocus();
            return;
        }
        
        if (cbLocation.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Lokasi Tujuan / Barang Keluar wajib dipilih!");
            alert.showAndWait();
            cbLocation.requestFocus();
            return;
        }
        
        if (dpLoanDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tanggal Keluar wajib diisi!");
            alert.showAndWait();
            dpLoanDate.requestFocus();
            return;
        }
        
        if (cbStatus.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Status Barang Keluar wajib dipilih!");
            alert.showAndWait();
            cbStatus.requestFocus();
            return;
        }
        
        int qtyOut = 0;
        try {
            qtyOut = Integer.parseInt(txtQty.getText());
            if (qtyOut <= 0) throw new Exception();
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Jumlah (Qty) harus berupa angka lebih dari 0!");
            alert.showAndWait();
            txtQty.requestFocus();
            return;
        }
        
        Asset selectedAsset = cbAsset.getValue();
        if ("ATK".equalsIgnoreCase(selectedAsset.getCategoryType()) && qtyOut > selectedAsset.getQuantity()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Stok tidak mencukupi! Sisa stok hanya " + selectedAsset.getQuantity());
            alert.showAndWait();
            txtQty.requestFocus();
            return;
        }
        
        if (txtPurpose.getText() == null || txtPurpose.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Keperluan wajib dijelaskan!");
            alert.showAndWait();
            txtPurpose.requestFocus();
            return;
        }

        // --- (JIKA SEMUA VALIDASI LOLOS) ---
        Loan loan = new Loan();
        loan.setPicName(txtPicName.getText());
        loan.setDivision(txtDivision.getText());
        loan.setAssetId(cbAsset.getValue().getId());
        loan.setLocation(cbLocation.getValue());
        loan.setLoanDate(dpLoanDate.getValue().toString());
        loan.setPurpose(txtPurpose.getText());
        loan.setQuantity(qtyOut);
        loan.setStatus(cbStatus.getValue());

        // --- SIMPAN KE DATABASE ---
        LoanDAO dao = new LoanDAO();
        if (dao.insert(loan)) {
            if ("ASET".equalsIgnoreCase(selectedAsset.getCategoryType())) {
                assetDao.updateStatus(loan.getAssetId(), loan.getStatus());
            } else {
                assetDao.deductQuantity(loan.getAssetId(), loan.getQuantity());
                
                // Cek apakah stok jadi habis, jika ya ubah status jadi Habis (Opsional)
                if (selectedAsset.getQuantity() - loan.getQuantity() == 0) {
                    assetDao.updateStatus(loan.getAssetId(), "Habis");
                }
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data transaksi keluar berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
            loadAssets(); // Refresh list to remove borrowed item
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data ke database!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        txtPicName.clear();
        txtDivision.clear();
        if(cbLocation != null) cbLocation.getSelectionModel().clearSelection();
        txtPurpose.clear();
        if(txtQty != null) { txtQty.clear(); txtQty.setDisable(true); }
        cbAsset.getSelectionModel().clearSelection();
        if(cbStatus != null) cbStatus.getSelectionModel().clearSelection();
        dpLoanDate.setValue(null);
    }
}