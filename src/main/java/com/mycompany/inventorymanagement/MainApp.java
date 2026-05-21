package com.mycompany.inventorymanagement;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        URL fxmlLocation = getClass().getResource("/fxml/Login.fxml");
        
        if (fxmlLocation == null) {
            System.err.println("ERROR: File Login.fxml tidak ditemukan di folder resources/fxml!");
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Login - Inventory");
        stage.setScene(scene);
        stage.setResizable(true); 
        stage.setMinWidth(1050);
        stage.setMinHeight(750);
        
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}