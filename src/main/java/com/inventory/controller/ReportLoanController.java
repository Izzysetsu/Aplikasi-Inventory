package com.inventory.controller;

import com.inventory.dao.LoanDAO;
import com.inventory.model.Loan;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportLoanController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Loan> tableLoan;
    @FXML private TableColumn<Loan, String> colPic, colDiv, colAsset, colLoc, colLoanDate, colReturnDate, colStatus;

    private LoanDAO loanDao = new LoanDAO();

    @FXML
    public void initialize() {
        // 1. Hubungkan Kolom FXML dengan variabel di Model Loan
        colPic.setCellValueFactory(new PropertyValueFactory<>("picName"));
        colDiv.setCellValueFactory(new PropertyValueFactory<>("division"));
        colAsset.setCellValueFactory(new PropertyValueFactory<>("assetDisplay"));
        colLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Tarik datanya
        loadData();
    }

    private void loadData() {
        List<Loan> data = loanDao.getAllLoans();
        tableLoan.getItems().setAll(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleExport() {
        // 1. Cek apakah tabel ada datanya
        if (tableLoan.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tidak ada data untuk di-export!");
            alert.showAndWait();
            return;
        }

        // 2. Buka jendela "Save As"
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Laporan_Peminjaman_Asset.xlsx");
        
        File file = fileChooser.showSaveDialog(tableLoan.getScene().getWindow());

        // 3. Proses Export jika user memilih lokasi penyimpanan
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Data Peminjaman");

                // Buat Header Kolom di Excel (Baris 0)
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Nama PIC");
                headerRow.createCell(1).setCellValue("Divisi");
                headerRow.createCell(2).setCellValue("Asset (Barcode)");
                headerRow.createCell(3).setCellValue("Lokasi");
                headerRow.createCell(4).setCellValue("Tgl Pinjam");
                headerRow.createCell(5).setCellValue("Tgl Kembali");
                headerRow.createCell(6).setCellValue("Status");

                // Masukkan Data dari Tabel ke Excel
                List<Loan> data = tableLoan.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1); // Mulai dari baris 1
                    Loan loan = data.get(i);

                    // Pengecekan null agar tidak error jika ada data kosong di database
                    row.createCell(0).setCellValue(loan.getPicName() != null ? loan.getPicName() : "");
                    row.createCell(1).setCellValue(loan.getDivision() != null ? loan.getDivision() : "");
                    row.createCell(2).setCellValue(loan.getAssetDisplay() != null ? loan.getAssetDisplay() : "");
                    row.createCell(3).setCellValue(loan.getLocation() != null ? loan.getLocation() : "");
                    row.createCell(4).setCellValue(loan.getLoanDate() != null ? loan.getLoanDate() : "");
                    row.createCell(5).setCellValue(loan.getReturnDate() != null ? loan.getReturnDate() : "-");
                    row.createCell(6).setCellValue(loan.getStatus() != null ? loan.getStatus() : "");
                }

                // Tulis ke File Excel
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Laporan berhasil di-export ke Excel!");
                alert.setTitle("Export Sukses");
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal meng-export data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}