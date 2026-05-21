package com.inventory.controller;

import com.inventory.dao.CategoryDAO;
import com.inventory.model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MasterCategoryController {
    @FXML private TextField txtCategoryName;
    @FXML private ComboBox<String> cbType; 
    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colName;
    @FXML private TableColumn<Category, String> colType; 
    @FXML private Button btnSave, btnUpdate, btnDelete;

    private CategoryDAO dao = new CategoryDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        tableCategory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        cbType.setItems(FXCollections.observableArrayList("ASET", "ATK"));
        colId.setCellValueFactory(new PropertyValueFactory<>("categoryId")); 
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type")); 

        loadData();
        tableCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getCategoryId(); 
                txtCategoryName.setText(newVal.getName());
                cbType.setValue(newVal.getType());  
                
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadData() {
        tableCategory.getItems().setAll(dao.getAll());
    }

    @FXML
    private void handleSave() {
        if (txtCategoryName.getText() == null || txtCategoryName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama Kategori tidak boleh kosong!");
            txtCategoryName.requestFocus(); 
            return; 
        }

        if (cbType.getValue() == null || cbType.getValue().trim().isEmpty() || cbType.getValue().equals("Pilih Tipe")) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tipe Kategori (ASET/ATK) wajib dipilih!");
            cbType.requestFocus();
            return; 
        }

        Category cat = new Category();
        cat.setName(txtCategoryName.getText());
        cat.setType(cbType.getValue()); 
        
        if (dao.insert(cat)) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Kategori berhasil disimpan!");
            handleClear(); 
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan data ke database!");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0) {
            if (txtCategoryName.getText().trim().isEmpty() || cbType.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Semua kolom form wajib diisi!");
                return;
            }

            Category cat = new Category();
            cat.setCategoryId(selectedId); 
            cat.setName(txtCategoryName.getText());
            cat.setType(cbType.getValue());
            
            if (dao.update(cat)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Kategori berhasil diubah!");
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah data!");
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus kategori ini?", ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText(null);
            confirmAlert.showAndWait();
            
            if (confirmAlert.getResult() == ButtonType.YES) {
                if (dao.delete(selectedId)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kategori berhasil dihapus (Soft Delete).");
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
        txtCategoryName.clear();
        cbType.getSelectionModel().clearSelection(); 
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableCategory.getSelectionModel().clearSelection();
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