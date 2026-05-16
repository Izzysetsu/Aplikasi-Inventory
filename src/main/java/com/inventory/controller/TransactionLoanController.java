package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.LoanDAO;
import com.inventory.model.Asset;
import com.inventory.model.Loan;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class TransactionLoanController {

    @FXML private TextField txtPicName;
    @FXML private TextField txtDivision;
    @FXML private ComboBox<Asset> cbAsset;
    @FXML private TextField txtLocation;
    @FXML private DatePicker dpLoanDate;
    @FXML private DatePicker dpReturnDate;
    @FXML private TextArea txtPurpose;

    private AssetDAO assetDao = new AssetDAO();

    @FXML
    public void initialize() {
        loadAssets();
        System.out.println("Halaman Peminjaman Berhasil Dibuka!");
    }

    private void loadAssets() {
        List<Asset> assets = assetDao.getAll();
        cbAsset.setItems(FXCollections.observableArrayList(assets));
        
        cbAsset.setCellFactory(lv -> new ListCell<Asset>() {
            @Override protected void updateItem(Asset item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : "[" + item.getBarcode() + "] " + item.getName());
            }
        });
        
        cbAsset.setButtonCell(new ListCell<Asset>() {
            @Override protected void updateItem(Asset item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : "[" + item.getBarcode() + "] " + item.getName());
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
        
        if (txtLocation.getText() == null || txtLocation.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Lokasi Tujuan / Peminjaman wajib diisi!");
            alert.showAndWait();
            txtLocation.requestFocus();
            return;
        }
        
        if (dpLoanDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tanggal Pinjam wajib diisi!");
            alert.showAndWait();
            dpLoanDate.requestFocus();
            return;
        }
        
        if (txtPurpose.getText() == null || txtPurpose.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Keperluan peminjaman wajib dijelaskan!");
            alert.showAndWait();
            txtPurpose.requestFocus();
            return;
        }

        // --- (JIKA SEMUA VALIDASI LOLOS) ---
        Loan loan = new Loan();
        loan.setPicName(txtPicName.getText());
        loan.setDivision(txtDivision.getText());
        loan.setAssetId(cbAsset.getValue().getId());
        loan.setLocation(txtLocation.getText());
        loan.setLoanDate(dpLoanDate.getValue().toString());
       
        if (dpReturnDate.getValue() != null) {
            loan.setReturnDate(dpReturnDate.getValue().toString());
        }
        
        loan.setPurpose(txtPurpose.getText());

        // --- SIMPAN KE DATABASE ---
        LoanDAO dao = new LoanDAO();
        if (dao.insert(loan)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data peminjaman berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data ke database!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        txtPicName.clear();
        txtDivision.clear();
        txtLocation.clear();
        txtPurpose.clear();
        cbAsset.getSelectionModel().clearSelection();
        dpLoanDate.setValue(null);
        dpReturnDate.setValue(null);
    }
}