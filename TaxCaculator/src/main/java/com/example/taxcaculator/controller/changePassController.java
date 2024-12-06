package com.example.taxcaculator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class changePassController {
    @FXML
    private PasswordField currentPass;
    @FXML
    private PasswordField newPass;
    @FXML
    private PasswordField newPassAgain;
    @FXML
    private Text errorCurrentPass;
    @FXML
    private Text errorNewPass;

    public void onInput(){
        errorCurrentPass.setText("");
        errorNewPass.setText("");
    }

    public void HandleChangePass(ActionEvent event) {
        errorCurrentPass.setText("");
        errorNewPass.setText("");
        if(currentPass.getText().isEmpty() || newPass.getText().isEmpty() || newPassAgain.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Cần nhập đầy đủ các thông tin nhập thiếu!");
            alert.showAndWait();
            return;
        }

        if (!checkCurrentPass() ) {
            errorCurrentPass.setText("Mật khẩu hiện tại không đúng");
            return;
        }

        if (!checkNewPassAgain()) {
            errorNewPass.setText("Mật khẩu mới không khớp");

            return;
        }

        if (updatePass(newPass.getText())) {
            showSuccessAndRedirect(event);
        }
    }

    public String getPassOfUser() {
        String curPass = "";
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "SELECT password FROM users WHERE user_id='" + AppController.getInstance().getUser().getUserId() + "'";
        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(query)) {
            if (rs.next()) {
                curPass = rs.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curPass;
    }

    public boolean checkCurrentPass() {
        return currentPass.getText().equals(getPassOfUser());
    }

    public boolean checkNewPassAgain() {

        return newPass.getText().equals(newPassAgain.getText());
    }

    public boolean updatePass(String newPass) {
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "UPDATE users SET password=? WHERE user_id='" + AppController.getInstance().getUser().getUserId() + "'";
        try (PreparedStatement pstm = conn.prepareStatement(query)) {
            pstm.setString(1, newPass);
            int row = pstm.executeUpdate();
            return row != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showSuccessAndRedirect(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Cập nhật mật khẩu thành công!");
        alert.showAndWait();

        try {
            AppController.getInstance().setUser(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kiemthu/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            String css = getClass().getResource("/com/example/kiemthu/view/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
