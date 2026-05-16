package com.inventory.view;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class MasterSupplierView {
    private SupplierDAO dao = new SupplierDAO();
    private TableView<Supplier> table = new TableView<>();
    private int selectedSupplierId = 0; // Untuk menyimpan ID saat tabel diklik

    public Pane getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // 1. Judul Form
        Label lblTitle = new Label("Manajemen Supplier (Full CRUD)");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 2. Kotak Input (Form)
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);

        TextField txtName = new TextField(); txtName.setPromptText("Nama Supplier");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("No. Telepon");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email");
        TextField txtAddress = new TextField(); txtAddress.setPromptText("Alamat");

        form.add(new Label("Nama:"), 0, 0); form.add(txtName, 1, 0);
        form.add(new Label("Telepon:"), 0, 1); form.add(txtPhone, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(txtEmail, 1, 2);
        form.add(new Label("Alamat:"), 0, 3); form.add(txtAddress, 1, 3);

        // 3. Tombol CRUD
        Button btnSave = new Button("Simpan");
        Button btnUpdate = new Button("Ubah");
        Button btnDelete = new Button("Hapus");
        Button btnClear = new Button("Batal");

        // Kondisi awal: Ubah dan Hapus dimatikan sebelum ada data yang dipilih
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        HBox hboxButtons = new HBox(10);
        hboxButtons.getChildren().addAll(btnSave, btnUpdate, btnDelete, btnClear);

        // EVENT: TOMBOL SIMPAN
        btnSave.setOnAction(e -> {
            Supplier sup = new Supplier();
            sup.setName(txtName.getText());
            sup.setPhone(txtPhone.getText());
            sup.setEmail(txtEmail.getText());
            sup.setAddress(txtAddress.getText());
            
            if(dao.insert(sup)) {
                loadData(); 
                clearForm(txtName, txtPhone, txtEmail, txtAddress, btnSave, btnUpdate, btnDelete);
            }
        });

        // EVENT: TOMBOL UBAH
        btnUpdate.setOnAction(e -> {
            if (selectedSupplierId != 0) {
                Supplier sup = new Supplier();
                sup.setId(selectedSupplierId); // ID tidak boleh kosong untuk Update
                sup.setName(txtName.getText());
                sup.setPhone(txtPhone.getText());
                sup.setEmail(txtEmail.getText());
                sup.setAddress(txtAddress.getText());
                
                if(dao.update(sup)) {
                    loadData();
                    clearForm(txtName, txtPhone, txtEmail, txtAddress, btnSave, btnUpdate, btnDelete);
                }
            }
        });

        // EVENT: TOMBOL HAPUS (Soft Delete)
        btnDelete.setOnAction(e -> {
            if (selectedSupplierId != 0) {
                // Munculkan popup konfirmasi
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus data ini?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                
                if (alert.getResult() == ButtonType.YES) {
                    if(dao.delete(selectedSupplierId)) {
                        loadData();
                        clearForm(txtName, txtPhone, txtEmail, txtAddress, btnSave, btnUpdate, btnDelete);
                    }
                }
            }
        });

        // EVENT: TOMBOL BATAL
        btnClear.setOnAction(e -> clearForm(txtName, txtPhone, txtEmail, txtAddress, btnSave, btnUpdate, btnDelete));

        // 4. Tabel untuk menampilkan data
        TableColumn<Supplier, String> colName = new TableColumn<>("Nama");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Supplier, String> colPhone = new TableColumn<>("Telepon");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Supplier, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(colName, colPhone, colEmail);
        loadData();

        // EVENT: KLIK BARIS TABEL (Pindahkan data ke form)
        table.setOnMouseClicked(e -> {
            Supplier selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedSupplierId = selected.getId();
                txtName.setText(selected.getName());
                txtPhone.setText(selected.getPhone());
                txtEmail.setText(selected.getEmail());
                txtAddress.setText(selected.getAddress());
                
                // Ubah status tombol
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        // Gabungkan semuanya
        layout.getChildren().addAll(lblTitle, form, hboxButtons, table);
        return layout;
    }

    private void loadData() {
        table.getItems().setAll(dao.getAll());
    }

    // Fungsi untuk membersihkan layar setelah eksekusi
    private void clearForm(TextField n, TextField p, TextField e, TextField a, Button btnS, Button btnU, Button btnD) {
        selectedSupplierId = 0;
        n.clear(); p.clear(); e.clear(); a.clear();
        btnS.setDisable(false); 
        btnU.setDisable(true);
        btnD.setDisable(true);
        table.getSelectionModel().clearSelection();
    }
}