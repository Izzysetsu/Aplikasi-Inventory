package com.inventory.controller;

import com.inventory.dao.RoomBookingDAO;
import com.inventory.model.RoomBooking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportRoomBookingController {

    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private TableView<RoomBooking> tableBooking;
    @FXML private TableColumn<RoomBooking, String> colRoom, colStartTime, colEndTime, colPic, colDivision, colPosition, colPurpose, colStatus;
    @FXML private TableColumn<RoomBooking, Integer> colParticipants;

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
        colParticipants.setCellValueFactory(new PropertyValueFactory<>("participants"));
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
}
