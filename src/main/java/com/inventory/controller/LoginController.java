package com.inventory.controller;

import com.inventory.dao.UserDAO;
import com.inventory.model.User;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private UserDAO userDao = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username dan Password harus diisi!");
            lblError.setVisible(true);
            return;
        }

        User user = userDao.login(username, password);

        if (user != null) {
            lblError.setVisible(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainDashboard.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) btnLogin.getScene().getWindow(); 
                stage.setScene(new Scene(root)); 
                stage.setTitle("Inventory Management IJI - Dashboard");
                stage.centerOnScreen(); 
                
            } catch (IOException e) {
                e.printStackTrace();
                lblError.setText("Error memuat halaman Dashboard!");
                lblError.setVisible(true);
            }
        } else {
            // JIKA GAGAL: Munculkan teks merah
            lblError.setText("Username atau Password salah!");
            lblError.setVisible(true);
        }
    }
}