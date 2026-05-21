package com.inventory.controller;

import com.inventory.dao.LocationDAO;
import com.inventory.model.Location;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MasterLocationController {

    @FXML private TextField txtLocationName;
    @FXML private TextArea txtDescription;
    @FXML private Button btnSave, btnUpdate, btnDelete;
    
    @FXML private TableView<Location> tableLocation;
    @FXML private TableColumn<Location, Integer> colId;
    @FXML private TableColumn<Location, String> colName;
    @FXML private TableColumn<Location, String> colDescription;

    private LocationDAO dao = new LocationDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        // Force tabel agar kolomnya otomatis melebar memenuhi layar (Pengganti FXML)
        tableLocation.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Setup Kolom Tabel
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();

        // Event Klik Baris Tabel
        tableLocation.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getId();
                txtLocationName.setText(newVal.getName());
                txtDescription.setText(newVal.getDescription());

                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadData() {
        tableLocation.getItems().setAll(dao.getAll());
    }

    @FXML
    private void handleSave() {
        if (txtLocationName.getText() == null || txtLocationName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama Lokasi tidak boleh kosong!");
            alert.showAndWait();
            txtLocationName.requestFocus();
            return;
        }

        Location loc = new Location();
        loc.setName(txtLocationName.getText());
        loc.setDescription(txtDescription.getText() != null ? txtDescription.getText() : "");

        if (dao.insert(loc)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Lokasi berhasil disimpan!");
            alert.showAndWait();
            handleClear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data lokasi!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0) {
            Location loc = new Location();
            loc.setId(selectedId);
            loc.setName(txtLocationName.getText());
            loc.setDescription(txtDescription.getText());

            if (dao.update(loc)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Lokasi berhasil diubah!");
                alert.showAndWait();
                handleClear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengubah data Lokasi!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus lokasi ini?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (dao.delete(selectedId)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Data Lokasi berhasil dihapus!");
                    successAlert.showAndWait();
                    handleClear();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus data Lokasi!");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedId = 0;
        txtLocationName.clear();
        txtDescription.clear();
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableLocation.getSelectionModel().clearSelection();
        loadData();
    }
}