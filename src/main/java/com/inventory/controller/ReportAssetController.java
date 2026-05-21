package com.inventory.controller;

import com.inventory.dao.AssetHistoryDAO;
import com.inventory.model.AssetHistory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportAssetController {

    @FXML private TextField txtSearch;
    @FXML private DatePicker dpStartDate, dpEndDate;
    @FXML private TableView<AssetHistory> tableOutbound;
    @FXML private TableColumn<AssetHistory, String> colDate, colBarcode, colAsset, colTransType, colLocation, colPurpose;
    @FXML private TableColumn<AssetHistory, Integer> colQty;

    private AssetHistoryDAO historyDao = new AssetHistoryDAO();
    private List<AssetHistory> allData;

    @FXML
    public void initialize() {
        if (tableOutbound != null) {
            tableOutbound.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        colDate.setCellValueFactory(new PropertyValueFactory<>("transDate"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcodeCode"));
        colAsset.setCellValueFactory(new PropertyValueFactory<>("assetName"));
        colTransType.setCellValueFactory(new PropertyValueFactory<>("transType"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        loadData();
    }

    private void loadData() {
        allData = historyDao.getAllHistory();
        tableOutbound.setItems(FXCollections.observableArrayList(allData));
    }

    @FXML
    private void handleFilter() {
        String keyword = txtSearch.getText().toLowerCase();
        LocalDate start = dpStartDate.getValue();
        LocalDate end = dpEndDate.getValue();

        List<AssetHistory> filtered = allData.stream().filter(o -> {
            boolean matchKeyword = keyword.isEmpty() || 
                (o.getAssetName() != null && o.getAssetName().toLowerCase().contains(keyword)) ||
                (o.getLocation() != null && o.getLocation().toLowerCase().contains(keyword)) ||
                (o.getBarcodeCode() != null && o.getBarcodeCode().toLowerCase().contains(keyword)) ||
                (o.getTransType() != null && o.getTransType().toLowerCase().contains(keyword));
            
            boolean matchDate = true;
            if (start != null || end != null) {
                try {
                    LocalDate oDate = LocalDate.parse(o.getTransDate());
                    if (start != null && oDate.isBefore(start)) matchDate = false;
                    if (end != null && oDate.isAfter(end)) matchDate = false;
                } catch (Exception e) {
                    // Ignore parsing error
                }
            }
            return matchKeyword && matchDate;
        }).collect(Collectors.toList());

        tableOutbound.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        txtSearch.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        loadData();
    }

    @FXML
    private void handleExportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Riwayat Barang");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Laporan_Riwayat_Barang_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(tableOutbound.getScene().getWindow());
        
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Riwayat Barang");
                
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Tanggal");
                header.createCell(1).setCellValue("Barcode");
                header.createCell(2).setCellValue("Nama Barang");
                header.createCell(3).setCellValue("Jenis Transaksi");
                header.createCell(4).setCellValue("Qty");
                header.createCell(5).setCellValue("Lokasi");
                header.createCell(6).setCellValue("Keterangan");
                
                int rowIdx = 1;
                for (AssetHistory o : tableOutbound.getItems()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(o.getTransDate());
                    row.createCell(1).setCellValue(o.getBarcodeCode());
                    row.createCell(2).setCellValue(o.getAssetName());
                    row.createCell(3).setCellValue(o.getTransType());
                    row.createCell(4).setCellValue(o.getQuantity());
                    row.createCell(5).setCellValue(o.getLocation());
                    row.createCell(6).setCellValue(o.getPurpose());
                }
                
                for (int i = 0; i < 7; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data berhasil diexport ke Excel!");
                alert.showAndWait();
                
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengexport file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
