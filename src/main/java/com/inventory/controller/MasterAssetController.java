package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.CategoryDAO;
import com.inventory.model.Asset;
import com.inventory.model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class MasterAssetController {

    @FXML private TextField txtBarcode, txtName;
    @FXML private TextArea txtSpec;
    @FXML private ComboBox<Category> cbCategory; // Menggunakan objek Category
    @FXML private ComboBox<String> cbStatus;
    @FXML private TableView<Asset> tableAsset;
    @FXML private TableColumn<Asset, String> colBarcode, colName, colCategory, colStatus, colSpec;

    private AssetDAO assetDao = new AssetDAO();
    private CategoryDAO catDao = new CategoryDAO();

    @FXML
    public void initialize() {
        // 1. Setup Kolom Tabel
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specification"));

        // 2. Isi ComboBox Status sesuai Flowchart
        cbStatus.setItems(FXCollections.observableArrayList("Idle", "Dipinjam", "Rusak", "Setup", "Booking", "Service"));

        // 3. Isi ComboBox Kategori dari Database
        loadCategories();
        
        loadData();
    }

    private void loadCategories() {
        List<Category> categories = catDao.getAll();
        cbCategory.setItems(FXCollections.observableArrayList(categories));
        
        // Agar ComboBox menampilkan Nama Kategori saja
        cbCategory.setCellFactory(lv -> new ListCell<Category>() {
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
        cbCategory.setButtonCell(new ListCell<Category>() {
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
    }

    private void loadData() {
        tableAsset.getItems().setAll(assetDao.getAll());
    }

    @FXML
    private void handleSave() {
        // --- PROSES VALIDASI ---
        if (txtBarcode.getText() == null || txtBarcode.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Barcode / Kode Barang tidak boleh kosong!");
            alert.showAndWait();
            txtBarcode.requestFocus();
            return;
        }
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama Asset tidak boleh kosong!");
            alert.showAndWait();
            txtName.requestFocus();
            return;
        }
        if (cbCategory.getValue() == null || cbCategory.getValue().toString().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Kategori wajib dipilih!");
            alert.showAndWait();
            cbCategory.requestFocus();
            return;
        }

        // --- PROSES SIMPAN ---
        Asset asset = new Asset();
        asset.setBarcode(txtBarcode.getText());
        asset.setName(txtName.getText());


        if (assetDao.insert(asset)) { 
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Asset berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
           
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data Asset!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        txtBarcode.clear(); txtName.clear(); txtSpec.clear();
        cbCategory.getSelectionModel().clearSelection();
        cbStatus.getSelectionModel().clearSelection();
        loadData();
    }
}