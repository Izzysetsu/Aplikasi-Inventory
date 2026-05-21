package com.inventory.controller;

import com.inventory.dao.RoomBookingDAO;
import com.inventory.model.RoomBooking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

public class ReportRoomBookingController {

    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private TableView<RoomBooking> tableBooking;
    @FXML private TableColumn<RoomBooking, String> colRoom, colStartTime, colEndTime, colPic, colDivision, colPurpose, colStatus;

    private RoomBookingDAO bookingDao = new RoomBookingDAO();

    @FXML
    public void initialize() {
        if (tableBooking != null) {
            tableBooking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colPic.setCellValueFactory(new PropertyValueFactory<>("picName"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        cbStatusFilter.setItems(FXCollections.observableArrayList("Semua Status", "Approved", "Completed"));
        cbStatusFilter.getSelectionModel().selectFirst();

        loadData();
    }

    private void loadData() {
        String filter = cbStatusFilter.getValue();
        if (filter == null || "Semua Status".equals(filter)) {
            tableBooking.setItems(FXCollections.observableArrayList(bookingDao.getAll()));
        } else {
            tableBooking.setItems(FXCollections.observableArrayList(bookingDao.getByStatus(filter)));
        }
    }

    @FXML
    private void handleFilter() {
        loadData();
    }

    @FXML
    private void handleExportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(tableBooking.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Laporan Peminjaman Ruangan");

                // Header Row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"No", "Ruangan", "Waktu Mulai", "Waktu Selesai", "Nama Peminjam", "Divisi", "Keperluan", "Status"};
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    CellStyle style = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    style.setFont(font);
                    cell.setCellStyle(style);
                }

                // Data Rows
                int rowNum = 1;
                for (RoomBooking booking : tableBooking.getItems()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rowNum - 1);
                    row.createCell(1).setCellValue(booking.getRoomName() != null ? booking.getRoomName() : "");
                    row.createCell(2).setCellValue(booking.getStartTime() != null ? booking.getStartTime() : "");
                    row.createCell(3).setCellValue(booking.getEndTime() != null ? booking.getEndTime() : "");
                    row.createCell(4).setCellValue(booking.getPicName() != null ? booking.getPicName() : "");
                    row.createCell(5).setCellValue(booking.getDivision() != null ? booking.getDivision() : "");
                    row.createCell(6).setCellValue(booking.getPurpose() != null ? booking.getPurpose() : "");
                    row.createCell(7).setCellValue(booking.getStatus() != null ? booking.getStatus() : "");
                }

                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Export ke Excel berhasil!");
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengexport file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
