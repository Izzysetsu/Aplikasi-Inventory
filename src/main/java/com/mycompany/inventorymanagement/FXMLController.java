package com.mycompany.inventorymanagement;

import java.net.URL;
import java.util.Optional; 
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.fxml.Initializable;
import javafx.scene.Node; 
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.scene.control.Alert; 
import javafx.scene.control.ButtonType; 
import javafx.scene.control.Label;
import javafx.stage.Stage; 

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    // ================= LOGOUT =================
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin keluar dari sistem?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage dashboardStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                dashboardStage.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                
                Stage loginStage = new Stage();
                loginStage.setTitle("Login - Inventory IJI");
                loginStage.setScene(new Scene(root));
                loginStage.centerOnScreen();
                loginStage.setResizable(false);
                loginStage.show();
                
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman Login.");
            }
        }
    }
    // =========================================================
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}