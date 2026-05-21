package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.CategoryDAO;
import com.inventory.dao.SupplierDAO;
import com.inventory.model.Asset;
import com.inventory.model.Category;
import com.inventory.model.Supplier;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.ArrayList;
import com.inventory.dao.StatusDAO;
import com.inventory.model.Status;

public class MasterAssetController {

    @FXML private TextField txtBarcode, txtName, txtSearch, txtQty, txtUnit;
    @FXML private TextArea txtSpec;
    @FXML private ComboBox<Category> cbCategory; 
    @FXML private ComboBox<Supplier> cbSupplier; 
    @FXML private ComboBox<String> cbStatus;
    
    @FXML private Button btnSave, btnUpdate, btnDelete, btnGenerate;
    
    @FXML private TableView<Asset> tableAsset;
    @FXML private TableColumn<Asset, String> colBarcode, colName, colCategory, colSupplier, colStatus, colSpec, colUnit;
    @FXML private TableColumn<Asset, Integer> colQty;

    private AssetDAO assetDao = new AssetDAO();
    private CategoryDAO catDao = new CategoryDAO();
    private SupplierDAO supDao = new SupplierDAO();
    private int selectedId = 0;

    @FXML
    public void initialize() {
        if (tableAsset != null) {
            tableAsset.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        // --- 1. SETUP KOLOM TABEL ---
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcodeCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specification"));

        // --- 2. SETUP COMBOBOX STATUS ---
        loadStatuses();

        // --- 3. LOAD DATA MASTER ---
        loadCategories();
        loadSuppliers();
        loadData();

        // --- 4. EVENT LISTENER TABEL ---
        tableAsset.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedId = newVal.getId();
                txtBarcode.setText(newVal.getBarcodeCode());
                txtName.setText(newVal.getName());
                txtSpec.setText(newVal.getSpecification());
                cbStatus.setValue(newVal.getStatus());
                if (txtQty != null) txtQty.setText(String.valueOf(newVal.getQuantity()));
                if (txtUnit != null) txtUnit.setText(newVal.getUnit());
                
                for (Category cat : cbCategory.getItems()) {
                    if (cat.getCategoryId() == newVal.getCategoryId()) {
                        cbCategory.setValue(cat);
                        break;
                    }
                }
                
                for (Supplier sup : cbSupplier.getItems()) {
                    if (sup.getId() == newVal.getSupplierId()) {
                        cbSupplier.setValue(sup);
                        break;
                    }
                }
                
                txtBarcode.setDisable(true); 
                btnGenerate.setDisable(true);
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        // --- 5. EVENT LISTENER SEARCH ---
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                tableAsset.getItems().setAll(assetDao.search(newVal));
            });
        }
    }

    private void loadCategories() {
        List<Category> categories = catDao.getAll();
        cbCategory.setItems(FXCollections.observableArrayList(categories));
        
        // Custom cell renderer  kategori
        cbCategory.setCellFactory(lv -> new ListCell<Category>() {
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        cbCategory.setButtonCell(new ListCell<Category>() {
            @Override protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        
        cbCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("ASET".equalsIgnoreCase(newVal.getType())) {
                    txtQty.setText("1");
                    txtQty.setDisable(true);
                    txtUnit.setText("pcs");
                    txtUnit.setDisable(true);
                    btnGenerate.setDisable(false);
                    txtBarcode.setDisable(false);
                } else if ("ATK".equalsIgnoreCase(newVal.getType())) {
                    txtQty.clear();
                    txtQty.setDisable(false);
                    txtUnit.clear();
                    txtUnit.setDisable(false);
                    txtBarcode.clear();
                    txtBarcode.setDisable(true);
                    btnGenerate.setDisable(true);
                }
            }
        });
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = supDao.getAll();
        cbSupplier.setItems(FXCollections.observableArrayList(suppliers));
        
        // Custom cell renderer  supplier
        cbSupplier.setCellFactory(lv -> new ListCell<Supplier>() {
            @Override protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        cbSupplier.setButtonCell(new ListCell<Supplier>() {
            @Override protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    private void loadData() {
        tableAsset.getItems().setAll(assetDao.getAll());
    }

    private void loadStatuses() {
        StatusDAO statusDao = new StatusDAO();
        List<String> statusNames = new ArrayList<>();
        for (Status st : statusDao.getAll()) {
            statusNames.add(st.getName());
        }
        cbStatus.setItems(FXCollections.observableArrayList(statusNames));
    }

    @FXML
    private void handleGenerateBarcode() {
        String autoCode = assetDao.generateAssetCode();
        txtBarcode.setText(autoCode);
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        Asset asset = new Asset();
        asset.setBarcodeCode(txtBarcode.getText());
        asset.setName(txtName.getText());
        asset.setCategoryId(cbCategory.getValue().getCategoryId());
        
        if (cbSupplier.getValue() != null) {
            asset.setSupplierId(cbSupplier.getValue().getId());
        } else {
            asset.setSupplierId(0);
        }
        
        asset.setStatus(cbStatus.getValue());
        asset.setSpecification(txtSpec.getText() != null ? txtSpec.getText() : "");
        
        int qty = 1;
        if (txtQty.getText() != null && !txtQty.getText().isEmpty()) {
            try { qty = Integer.parseInt(txtQty.getText()); } catch(Exception e){}
        }
        asset.setQuantity(qty);
        asset.setUnit(txtUnit.getText() != null && !txtUnit.getText().isEmpty() ? txtUnit.getText() : "pcs");

        if (assetDao.insert(asset)) { 
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Asset berhasil disimpan!");
            alert.showAndWait();
            handleClear(); 
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan data Asset.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedId != 0 && validateInput()) {
            Asset asset = new Asset();
            asset.setId(selectedId);
            asset.setBarcodeCode(txtBarcode.getText());
            asset.setName(txtName.getText());
            asset.setCategoryId(cbCategory.getValue().getCategoryId());
            
            if (cbSupplier.getValue() != null) {
                asset.setSupplierId(cbSupplier.getValue().getId());
            } else {
                asset.setSupplierId(0);
            }
            
            asset.setStatus(cbStatus.getValue());
            asset.setSpecification(txtSpec.getText() != null ? txtSpec.getText() : "");
            
            int qty = 1;
            if (txtQty.getText() != null && !txtQty.getText().isEmpty()) {
                try { qty = Integer.parseInt(txtQty.getText()); } catch(Exception e){}
            }
            asset.setQuantity(qty);
            asset.setUnit(txtUnit.getText() != null && !txtUnit.getText().isEmpty() ? txtUnit.getText() : "pcs");

            if (assetDao.update(asset)) { 
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data Asset berhasil diperbarui!");
                alert.showAndWait();
                handleClear(); 
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal diperbarui.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Hapus data asset ini?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (assetDao.delete(selectedId)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Data Asset berhasil dihapus!");
                    successAlert.showAndWait();
                    handleClear();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus data Asset!");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedId = 0;
        txtBarcode.clear(); 
        txtName.clear(); 
        txtSpec.clear();
        if(txtQty != null) { txtQty.clear(); txtQty.setDisable(true); }
        if(txtUnit != null) { txtUnit.clear(); txtUnit.setDisable(true); }
        if(txtSearch != null) txtSearch.clear();
        cbCategory.getSelectionModel().clearSelection();
        cbSupplier.getSelectionModel().clearSelection();
        cbStatus.getSelectionModel().clearSelection();
        
        txtBarcode.setDisable(false);
        btnGenerate.setDisable(false);
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableAsset.getSelectionModel().clearSelection();
        loadData();
    }

    private boolean validateInput() {
        String errorMsg = "";
        if (cbCategory.getValue() == null) errorMsg += "- Kategori harus dipilih\n";
        if (cbCategory.getValue() != null && "ASET".equalsIgnoreCase(cbCategory.getValue().getType()) && (txtBarcode.getText() == null || txtBarcode.getText().trim().isEmpty())) {
            errorMsg += "- Barcode wajib diisi untuk ASET\n";
        }
        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) errorMsg += "- Nama Barang wajib diisi\n";
        if (cbStatus.getValue() == null) errorMsg += "- Status Barang harus dipilih\n";
        
        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan lengkapi data berikut:\n" + errorMsg);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}