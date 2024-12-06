package com.example.taxcaculator.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertController {
    public static boolean alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alertType == Alert.AlertType.CONFIRMATION) {
            if(alert.getResult() == ButtonType.OK) {
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
}
