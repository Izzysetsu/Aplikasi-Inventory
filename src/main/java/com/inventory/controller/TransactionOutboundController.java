package com.inventory.controller;

import com.inventory.dao.AssetDAO;
import com.inventory.dao.LocationDAO;
import com.inventory.dao.OutboundDAO;
import com.inventory.model.Asset;
import com.inventory.model.Location;
import com.inventory.model.Outbound;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionOutboundController {

    @FXML private TextField txtSelectedAsset, txtQty, txtPurpose, txtSearch;
    @FXML private ComboBox<Location> cbLocation;
    @FXML private DatePicker dpOutboundDate;
    @FXML private Button btnSave;
    
    @FXML private TableView<Asset> tableAsset;
    @FXML private TableColumn<Asset, String> colBarcode, colName, colCategory, colStatus;
    @FXML private TableColumn<Asset, Integer> colQty;

    private AssetDAO assetDao = new AssetDAO();
    private LocationDAO locationDao = new LocationDAO();
    private OutboundDAO outboundDao = new OutboundDAO();
    
    private Asset selectedAsset = null;

    @FXML
    public void initialize() {
        if (tableAsset != null) {
            tableAsset.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcodeCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadLocations();
        loadAssets();
        dpOutboundDate.setValue(LocalDate.now());

        tableAsset.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedAsset = newVal;
                txtSelectedAsset.setText(newVal.getBarcodeCode() + " - " + newVal.getName());
                btnSave.setDisable(false);
                
                if ("ASET".equalsIgnoreCase(newVal.getCategoryType())) {
                    txtQty.setText("1");
                    txtQty.setDisable(true);
                } else {
                    txtQty.clear();
                    txtQty.setDisable(false);
                }
            }
        });

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                String keyword = newVal.toLowerCase();
                List<Asset> filtered = assetDao.getAll().stream()
                    .filter(a -> a.getQuantity() > 0 && 
                                 ("Idle".equalsIgnoreCase(a.getStatus()) || "Tersedia".equalsIgnoreCase(a.getStatus()) || "Baik".equalsIgnoreCase(a.getStatus())))
                    .filter(a -> (a.getName() != null && a.getName().toLowerCase().contains(keyword)) || 
                                 (a.getBarcodeCode() != null && a.getBarcodeCode().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
                tableAsset.getItems().setAll(filtered);
            });
        }
    }

    private void loadLocations() {
        List<Location> locations = locationDao.getAll();
        cbLocation.setItems(FXCollections.observableArrayList(locations));
        
        cbLocation.setCellFactory(lv -> new ListCell<Location>() {
            @Override protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        cbLocation.setButtonCell(new ListCell<Location>() {
            @Override protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    private void loadAssets() {
        List<Asset> availableAssets = assetDao.getAll().stream()
            .filter(a -> a.getQuantity() > 0 && 
                         ("Idle".equalsIgnoreCase(a.getStatus()) || "Tersedia".equalsIgnoreCase(a.getStatus()) || "Baik".equalsIgnoreCase(a.getStatus())))
            .collect(Collectors.toList());
        tableAsset.getItems().setAll(availableAssets);
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        int qty = Integer.parseInt(txtQty.getText());
        if (qty > selectedAsset.getQuantity()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Stok tidak mencukupi! Sisa stok saat ini: " + selectedAsset.getQuantity());
            alert.showAndWait();
            return;
        }

        Outbound outbound = new Outbound();
        outbound.setAssetId(selectedAsset.getId());
        outbound.setLocationId(cbLocation.getValue().getId());
        outbound.setQuantity(qty);
        outbound.setOutboundDate(dpOutboundDate.getValue().toString());
        outbound.setPurpose(txtPurpose.getText() != null ? txtPurpose.getText() : "");

        if (outboundDao.insert(outbound)) {
            // Deduct quantity in asset table
            assetDao.deductQuantity(selectedAsset.getId(), qty);
            
            // If asset is completely moved and it's ASET, change status maybe?
            // User did not explicitly request status change for Outbound, only stock decrement
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Proses Barang Keluar berhasil dicatat!");
            alert.showAndWait();
            handleClear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mencatat Barang Keluar.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClear() {
        selectedAsset = null;
        txtSelectedAsset.clear();
        txtQty.clear();
        txtQty.setDisable(false);
        txtPurpose.clear();
        txtSearch.clear();
        cbLocation.getSelectionModel().clearSelection();
        dpOutboundDate.setValue(LocalDate.now());
        btnSave.setDisable(true);
        tableAsset.getSelectionModel().clearSelection();
        loadAssets();
    }

    private boolean validateInput() {
        String errorMsg = "";
        if (selectedAsset == null) errorMsg += "- Pilih barang dari tabel\n";
        if (cbLocation.getValue() == null) errorMsg += "- Pilih Lokasi Tujuan\n";
        if (dpOutboundDate.getValue() == null) errorMsg += "- Isi Tanggal Keluar\n";
        
        if (txtQty.getText() == null || txtQty.getText().trim().isEmpty()) {
            errorMsg += "- Isi Jumlah (Qty)\n";
        } else {
            try {
                int q = Integer.parseInt(txtQty.getText());
                if (q <= 0) errorMsg += "- Jumlah (Qty) harus lebih dari 0\n";
            } catch (NumberFormatException e) {
                errorMsg += "- Jumlah (Qty) harus berupa angka\n";
            }
        }
        
        if (!errorMsg.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Silakan lengkapi data:\n" + errorMsg);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
