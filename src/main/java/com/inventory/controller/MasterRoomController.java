package com.inventory.controller;

import com.inventory.dao.RoomDAO;
import com.inventory.model.Room;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MasterRoomController {

    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    
    @FXML private Button btnSave, btnUpdate, btnDelete;
    
    @FXML private TableView<Room> tableRoom;
    @FXML private TableColumn<Room, String> colName;
    @FXML private TableColumn<Room, String> colDescription;

    private RoomDAO roomDao = new RoomDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        if (tableRoom != null) {
            tableRoom.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableRoom.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getRoomId();
                txtName.setText(newVal.getName());
                txtDescription.setText(newVal.getDescription());
                
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        loadData();
    }

    private void loadData() {
        tableRoom.setItems(FXCollections.observableArrayList(roomDao.getAll()));
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        Room room = new Room();
        room.setName(txtName.getText());
        room.setDescription(txtDescription.getText());

        if (roomDao.insert(room)) { 
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Ruangan berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data Ruangan.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0 && validateInput()) {
            Room room = new Room();
            room.setRoomId(selectedId);
            room.setName(txtName.getText());
            room.setDescription(txtDescription.getText());

            if (roomDao.update(room)) { 
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Ruangan berhasil diperbarui!");
                alert.showAndWait();
                handleClear(); 
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal memperbarui data Ruangan.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus ruangan ini?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (roomDao.delete(selectedId)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Ruangan berhasil dihapus!");
                    successAlert.showAndWait();
                    handleClear();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus ruangan!");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedId = 0;
        txtName.clear();
        txtDescription.clear();
        
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableRoom.getSelectionModel().clearSelection();
        loadData();
    }

    private boolean validateInput() {
        String errorMsg = "";
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            errorMsg += "- Nama Ruangan wajib diisi\n";
        }
        if (txtDescription.getText() == null || txtDescription.getText().trim().isEmpty()) {
            errorMsg += "- Keterangan Ruangan wajib diisi\n";
        }
        
        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan periksa inputan Anda:\n" + errorMsg);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
