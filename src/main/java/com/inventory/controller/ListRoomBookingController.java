package com.inventory.controller;

import com.inventory.dao.RoomBookingDAO;
import com.inventory.model.RoomBooking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ListRoomBookingController {

    @FXML private TableView<RoomBooking> tableBooking;
    @FXML private TableColumn<RoomBooking, String> colRoom, colStartTime, colEndTime, colPic, colDivision, colPosition, colPurpose;

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
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        loadData();
    }

    private void loadData() {
        // Hanya ambil yang statusnya Approved (Aktif)
        List<RoomBooking> activeBookings = bookingDao.getByStatus("Approved");
        tableBooking.setItems(FXCollections.observableArrayList(activeBookings));
    }

    @FXML
    private void handleCompleteBooking() {
        RoomBooking selected = tableBooking.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tandai peminjaman ruangan ini sebagai SELESAI?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (bookingDao.updateStatus(selected.getBookingId(), "Completed")) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Peminjaman berhasil diselesaikan!");
                    successAlert.showAndWait();
                    loadData();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Gagal menyelesaikan peminjaman!");
                    errorAlert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih salah satu data booking terlebih dahulu!");
            alert.showAndWait();
        }
    }
}
