package com.inventory.controller;

import com.inventory.dao.CategoryDAO;
import com.inventory.model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MasterCategoryController {

    @FXML private TextField txtCategoryName;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colName;
    @FXML private TableColumn<Category, String> colStatus;
    @FXML private Button btnSave, btnUpdate, btnDelete;

    private CategoryDAO dao = new CategoryDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        // Isi pilihan ComboBox
        cbStatus.setItems(FXCollections.observableArrayList("active", "inactive"));
        
        // Setup Kolom Tabel
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();

        // Event Klik Tabel
        tableCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getId();
                txtCategoryName.setText(newVal.getName());
                cbStatus.setValue(newVal.getStatus());
                
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
        
        // Cek Nama Kategori
        if (txtCategoryName.getText() == null || txtCategoryName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama Kategori tidak boleh kosong!");
            alert.setTitle("Peringatan");
            alert.showAndWait();
            txtCategoryName.requestFocus(); 
            return; 
        }

        
        if (cbStatus.getValue() == null || cbStatus.getValue().toString().trim().isEmpty() || cbStatus.getValue().toString().equals("Pilih Status")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Status Kategori wajib dipilih!");
            alert.setTitle("Peringatan");
            alert.showAndWait();
            cbStatus.requestFocus();
            return; 
        }

       
        
        Category cat = new Category();
        cat.setName(txtCategoryName.getText());
        cat.setStatus(cbStatus.getValue().toString()); 
        
        if (dao.insert(cat)) {
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Kategori berhasil disimpan!");
            alert.setTitle("Sukses");
            alert.showAndWait();
            
            handleClear(); 
             
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data ke database!");
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0) {
            Category cat = new Category();
            cat.setId(selectedId);
            cat.setName(txtCategoryName.getText());
            cat.setStatus(cbStatus.getValue());
            if (dao.update(cat)) {
                handleClear();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus kategori ini?", ButtonType.YES, ButtonType.NO);
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
        txtCategoryName.clear();
        cbStatus.getSelectionModel().clearSelection();
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableCategory.getSelectionModel().clearSelection();
        loadData();
    }
}