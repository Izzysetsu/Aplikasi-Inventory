package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.LoanDAO;
import com.inventory.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ListPeminjamanController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Loan> tableLoan;
    @FXML private TableColumn<Loan, String> colPicName, colDivision, colAsset, colLoanDate, colPurpose;
    
    @FXML private Label lblPicName, lblAsset, lblLoanDate;
    @FXML private DatePicker dpReturnDate;
    @FXML private Button btnReturn;

    private LoanDAO loanDao = new LoanDAO();
    private AssetDAO assetDao = new AssetDAO();
    private Loan selectedLoan = null;

    @FXML
    public void initialize() {
        tableLoan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        colPicName.setCellValueFactory(new PropertyValueFactory<>("picName"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        colAsset.setCellValueFactory(new PropertyValueFactory<>("assetDisplay"));
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        tableLoan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLoan = newVal;
                lblPicName.setText(newVal.getPicName() + " (" + newVal.getDivision() + ")");
                lblAsset.setText(newVal.getAssetDisplay());
                lblLoanDate.setText(newVal.getLoanDate());
                
                dpReturnDate.setDisable(false);
                dpReturnDate.setValue(LocalDate.now());
                btnReturn.setDisable(false);
            } else {
                clearSelection();
            }
        });

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

        loadData();
    }

    private void loadData() {
        List<Loan> activeLoans = loanDao.getLoansByStatus("Dipinjam");
        tableLoan.getItems().setAll(activeLoans);
        clearSelection();
    }
    
    private void filterData(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadData();
            return;
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Loan> filtered = loanDao.getLoansByStatus("Dipinjam").stream()
            .filter(l -> (l.getPicName() != null && l.getPicName().toLowerCase().contains(lowerCaseKeyword)) || 
                         (l.getAssetDisplay() != null && l.getAssetDisplay().toLowerCase().contains(lowerCaseKeyword)) ||
                         (l.getDivision() != null && l.getDivision().toLowerCase().contains(lowerCaseKeyword)))
            .collect(Collectors.toList());
        tableLoan.getItems().setAll(filtered);
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadData();
    }

    private void clearSelection() {
        selectedLoan = null;
        lblPicName.setText("-");
        lblAsset.setText("Belum ada yang dipilih");
        lblLoanDate.setText("-");
        dpReturnDate.setValue(null);
        dpReturnDate.setDisable(true);
        btnReturn.setDisable(true);
    }

    @FXML
    private void handleReturn() {
        if (selectedLoan == null) return;
        
        if (dpReturnDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih tanggal pengembalian terlebih dahulu!");
            alert.showAndWait();
            dpReturnDate.requestFocus();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Konfirmasi pengembalian barang ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        
        if (confirm.getResult() == ButtonType.YES) {
            String returnDateStr = dpReturnDate.getValue().toString();
            
            // Update transaksi peminjaman menjadi Selesai
            if (loanDao.updateReturnDateAndStatus(selectedLoan.getId(), returnDateStr, "Selesai")) {
                
                // Update status Aset kembali ke Idle
                assetDao.updateStatus(selectedLoan.getAssetId(), "Idle");
                
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Pengembalian barang berhasil diproses!");
                success.showAndWait();
                
                loadData();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Gagal memproses pengembalian!");
                error.showAndWait();
            }
        }
    }
}
