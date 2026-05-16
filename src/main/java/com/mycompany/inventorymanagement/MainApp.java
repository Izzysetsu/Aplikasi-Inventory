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
        
     
        URL fxmlLocation = getClass().getResource("/fxml/MainDashboard.fxml");
        
        if (fxmlLocation == null) {
            System.err.println("ERROR: File MainDashboard.fxml tidak ditemukan di folder resources/fxml!");
            return;
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        
       
        Scene scene = new Scene(root, 1100, 700);
        
        stage.setTitle("Inventory IJI - Menu Utama");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}