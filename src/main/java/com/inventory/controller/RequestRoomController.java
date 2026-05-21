package com.inventory.controller;

import com.inventory.dao.RoomBookingDAO;
import com.inventory.dao.RoomDAO;
import com.inventory.model.Room;
import com.inventory.model.RoomBooking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RequestRoomController {

    @FXML private ComboBox<Room> cbRoom;
    @FXML private DatePicker dpDate, dpEndDate;
    @FXML private ComboBox<String> cbStartTime, cbEndTime;
    @FXML private TextField txtPic, txtDivision;
    @FXML private TextArea txtPurpose;
    
    private RoomDAO roomDao = new RoomDAO();
    private RoomBookingDAO bookingDao = new RoomBookingDAO();

    @FXML
    public void initialize() {
        // Generate times
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int h = 7; h <= 21; h++) {
            times.add(String.format("%02d:00", h));
            times.add(String.format("%02d:30", h));
        }
        cbStartTime.setItems(times);
        cbEndTime.setItems(times);
        
        // Sync dpEndDate with dpDate since booking is max 1 day
        dpDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dpEndDate.setValue(newVal);
            }
        });
        
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms = roomDao.getAll();
        cbRoom.setItems(FXCollections.observableArrayList(rooms));
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        RoomBooking booking = new RoomBooking();
        booking.setRoomId(cbRoom.getValue().getRoomId());
        
        String dateStr = dpDate.getValue().toString(); // YYYY-MM-DD
        String startDateTime = dateStr + " " + cbStartTime.getValue() + ":00";
        String endDateTime = dateStr + " " + cbEndTime.getValue() + ":00";
        
        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        booking.setPicName(txtPic.getText());
        booking.setDivision(txtDivision.getText() != null ? txtDivision.getText() : "");
        booking.setPosition(""); // Default empty string since field removed
        booking.setParticipants(0); // Default 0 since field removed
        booking.setPurpose(txtPurpose.getText() != null ? txtPurpose.getText() : "");
        booking.setStatus("Approved");

        if (bookingDao.insert(booking)) { 
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Booking Ruangan berhasil diajukan dan disetujui!");
            alert.showAndWait();
            handleClear(); 
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengajukan booking.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        cbRoom.getSelectionModel().clearSelection();
        dpDate.setValue(null);
        dpEndDate.setValue(null);
        cbStartTime.getSelectionModel().clearSelection();
        cbEndTime.getSelectionModel().clearSelection();
        txtPic.clear();
        txtDivision.clear();
        txtPurpose.clear();
    }

    private boolean validateInput() {
        String errorMsg = "";
        if (cbRoom.getValue() == null) errorMsg += "- Ruangan harus dipilih\n";
        if (dpDate.getValue() == null) errorMsg += "- Tanggal booking harus dipilih\n";
        if (cbStartTime.getValue() == null) errorMsg += "- Jam Mulai harus dipilih\n";
        if (cbEndTime.getValue() == null) errorMsg += "- Jam Selesai harus dipilih\n";
        if (txtPic.getText() == null || txtPic.getText().trim().isEmpty()) errorMsg += "- Nama Peminjam wajib diisi\n";
        if (txtDivision.getText() == null || txtDivision.getText().trim().isEmpty()) errorMsg += "- Divisi wajib diisi\n";
        
        if (cbStartTime.getValue() != null && cbEndTime.getValue() != null) {
            LocalTime start = LocalTime.parse(cbStartTime.getValue());
            LocalTime end = LocalTime.parse(cbEndTime.getValue());
            if (!start.isBefore(end)) {
                errorMsg += "- Jam Mulai harus lebih awal dari Jam Selesai\n";
            }
        }
        
        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan periksa form:\n" + errorMsg);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
