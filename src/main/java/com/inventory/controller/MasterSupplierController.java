package com.inventory.controller;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MasterSupplierController {

    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtAddress;
    @FXML private Button btnSave, btnUpdate, btnDelete;
    @FXML private TableView<Supplier> tableSupplier;

    private SupplierDAO dao = new SupplierDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        loadData(); 
        tableSupplier.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedId = newSelection.getId();
                txtName.setText(newSelection.getName());
                txtPhone.setText(newSelection.getPhone());
                txtEmail.setText(newSelection.getEmail());
                txtAddress.setText(newSelection.getAddress());

                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadData() {
        tableSupplier.getItems().setAll(dao.getAll());
    }

    @FXML
    private void handleSave() {
        // --- PROSES VALIDASI ---
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama Supplier tidak boleh kosong!");
            alert.showAndWait();
            txtName.requestFocus();
            return;
        }
        if (txtPhone.getText() == null || txtPhone.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nomor Telepon tidak boleh kosong!");
            alert.showAndWait();
            txtPhone.requestFocus();
            return;
        }
        if (txtAddress.getText() == null || txtAddress.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Alamat Supplier tidak boleh kosong!");
            alert.showAndWait();
            txtAddress.requestFocus();
            return;
        }

        // --- PROSES SIMPAN ---
        Supplier sup = new Supplier();
        sup.setName(txtName.getText());
        sup.setPhone(txtPhone.getText());
        sup.setEmail(txtEmail.getText()); 
        sup.setAddress(txtAddress.getText());

        if (dao.insert(sup)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Supplier berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
            
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data Supplier!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0) {
            Supplier sup = new Supplier();
            sup.setId(selectedId);
            sup.setName(txtName.getText());
            sup.setPhone(txtPhone.getText());
            sup.setEmail(txtEmail.getText());
            sup.setAddress(txtAddress.getText());

            if (dao.update(sup)) {
                handleClear();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin hapus data?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (dao.delete(selectedId)) {
                    handleClear();
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedId = 0;
        txtName.clear(); txtPhone.clear(); txtEmail.clear(); txtAddress.clear();
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableSupplier.getSelectionModel().clearSelection();
        loadData();
    }
}