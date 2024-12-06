package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;
    private String userId="";
    private String role ="";





    public void HandleLogin (ActionEvent event) throws IOException{
        if (userName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText(null);
            alert.setContentText("Không được để tên đăng nhập trống!");
            alert.showAndWait();

            return;
        }

        if (password.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText(null);
            alert.setContentText("Không được để mật khẩu trống!");
            alert.showAndWait();

            return;
        }
        if(findUser()){
          if(AppController.getInstance().getUser().getRole().equals("Nhan Vien")){
                          root = FXMLLoader.load(getClass().getResource("/com/example/taxcaculator/view/staff/general.fxml"));
                      }else if(AppController.getInstance().getUser().getRole().equals("Truong phong")){
                root = FXMLLoader.load(getClass().getResource("/com/example/taxcaculator/view/departmentHead/general.fxml"));
            }else {
                root = FXMLLoader.load(getClass().getResource("/com/example/taxcaculator/view/accountant/listDepartments.fxml"));
            }
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi đăng nhập");
            alert.setHeaderText(null);
            alert.setContentText("Tên đăng nhập hoặc mật khẩu không chính xác!");
            alert.showAndWait();

            userName.setText("");
            password.setText("");
        }

    }

    public boolean findUser(){

        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "SELECT username, user_id, user_role FROM taxdb.users where username= ? and password= ?";
        PreparedStatement pstm = null;


        try{
            pstm = conn.prepareStatement(query);

            pstm.setString(1,userName.getText());
            pstm.setString(2,password.getText());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){

                role = rs.getString("user_role");
                userId = rs.getString("user_id");
                AppController.getInstance().setUser(new User(userId,role));
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

