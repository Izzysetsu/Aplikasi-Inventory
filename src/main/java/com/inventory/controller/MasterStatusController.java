package com.inventory.controller;

import com.inventory.dao.StatusDAO;
import com.inventory.model.Status;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MasterStatusController {
    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private TableView<Status> tableStatus;
    @FXML private TableColumn<Status, Integer> colId;
    @FXML private TableColumn<Status, String> colName;
    @FXML private TableColumn<Status, String> colDescription;
    @FXML private Button btnSave, btnUpdate, btnDelete;

    private StatusDAO dao = new StatusDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        tableStatus.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id")); 
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description")); 

        loadData();
        tableStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getId(); 
                txtName.setText(newVal.getName());
                txtDescription.setText(newVal.getDescription());  
                
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadData() {
        tableStatus.getItems().setAll(dao.getAll());
    }

    @FXML
    private void handleSave() {
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama Status tidak boleh kosong!");
            txtName.requestFocus(); 
            return; 
        }

        Status st = new Status();
        st.setName(txtName.getText());
        st.setDescription(txtDescription.getText());
        
        if (dao.insert(st)) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Status berhasil disimpan!");
            handleClear(); 
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan data ke database!");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0) {
            if (txtName.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama Status wajib diisi!");
                return;
            }

            Status st = new Status();
            st.setId(selectedId); 
            st.setName(txtName.getText());
            st.setDescription(txtDescription.getText());
            
            if (dao.update(st)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Status berhasil diubah!");
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah data!");
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus status ini?", ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText(null);
            confirmAlert.showAndWait();
            
            if (confirmAlert.getResult() == ButtonType.YES) {
                if (dao.delete(selectedId)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Status berhasil dihapus.");
                    handleClear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus data!");
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedId = 0;
        txtName.clear();
        if(txtDescription != null) txtDescription.clear();
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableStatus.getSelectionModel().clearSelection();
        loadData();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
