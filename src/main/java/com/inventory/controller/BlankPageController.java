package com.inventory.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BlankPageController {

    @FXML
    private Label lblTitle;

    public void setFeatureName(String featureName) {
        if (lblTitle != null) {
            lblTitle.setText("Menu " + featureName);
        }
    }
}
