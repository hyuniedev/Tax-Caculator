package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.Person;
import com.example.taxcaculator.model.PhongBan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class navBarController {

    public static PhongBan phongBan;
    @FXML
    private ImageView deletebtn;
    @FXML
    private AnchorPane generalScene;
    private Button selectedButton;
    @FXML
    private Text nameDepartment;
    @FXML
    private Text fullName;
    private simpleDepartmentController simpleDepartmentController;

    public void initialize(){
        if(phongBan!=null && nameDepartment!=null){
            String nameTP = "";
            ResultSet rs = QueryController.getInstance().Query("select fullname from taxdb.managers where manager_id = '"+phongBan.getIdTruongPhong()+"';");
            try {
                if(rs.next()){
                    nameTP = rs.getString("fullname");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            nameDepartment.setText(phongBan.getTenPhongBan()+" - Trưởng phòng: "+nameTP);
        }
        if(AppController.getInstance().getUser().getRole().equals("Nhan Vien") || AppController.getInstance().getUser().getRole().equals("Truong phong")){
            getNameUser();
        }
        if(deletebtn!=null){
            deletebtn.setOnMouseClicked(event -> {
                if(AlertController.alert(Alert.AlertType.CONFIRMATION,"Xác nhận","Bạn có chắc chắn xóa "+phongBan.getTenPhongBan()+" ?")){
                    try {
                        // Xóa các bản ghi từ bảng notifications và payroll liên quan đến employees
                        ResultSet rs = QueryController.getInstance().Query("select employee_id, user_id from taxdb.employees where department_id = '"+phongBan.getId()+"';");
                        while (rs.next()) {
                            String employeeId = rs.getString("employee_id");
                            String userId = rs.getString("user_id");

                            // Xóa dữ liệu trong bảng notifications liên quan đến employee_id
                            QueryController.getInstance().InsertValue("delete from taxdb.notifications where employee_id = '"+employeeId+"';");

                            // Xóa dữ liệu trong bảng payroll liên quan đến employee_id
                            QueryController.getInstance().InsertValue("delete from taxdb.payroll where employee_id = '"+employeeId+"';");

                            // Xóa dữ liệu trong bảng employees liên quan đến department_id
                            QueryController.getInstance().InsertValue("delete from taxdb.employees where employee_id = '"+employeeId+"';");

                            // Xóa dữ liệu trong bảng users liên quan đến user_id của employee
                            QueryController.getInstance().InsertValue("delete from taxdb.users where user_id = '"+userId+"';");
                        }
                        // Cuối cùng xóa dữ liệu trong bảng departments
                        QueryController.getInstance().InsertValue("delete from taxdb.departments where department_id = '"+phongBan.getId()+"';");
                        // Xóa các bản ghi từ bảng notifications và payroll liên quan đến managers
                        rs = QueryController.getInstance().Query("select manager_id, user_id from taxdb.managers where manager_id = '"+phongBan.getIdTruongPhong()+"';");
                        while (rs.next()) {
                            String managerId = rs.getString("manager_id");
                            String managerUserId = rs.getString("user_id");

                            // Xóa dữ liệu trong bảng notifications liên quan đến manager_id
                            QueryController.getInstance().InsertValue("delete from taxdb.notifications where manager_id = '"+managerId+"';");

                            // Xóa dữ liệu trong bảng payroll liên quan đến manager_id
                            QueryController.getInstance().InsertValue("delete from taxdb.payroll where manager_id = '"+managerId+"';");

                            // Xóa dữ liệu trong bảng managers
                            QueryController.getInstance().InsertValue("delete from taxdb.managers where manager_id = '"+managerId+"';");

                            // Xóa dữ liệu trong bảng users liên quan đến user_id của manager
                            QueryController.getInstance().InsertValue("delete from taxdb.users where user_id = '"+managerUserId+"';");
                        }

                        // Điều hướng lại danh sách department
                        gotoListDepartment(event);

                    } catch (IOException | SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
    }
    public void getNameUser(){
        String table ="";
        if(AppController.getInstance().getUser().getRole().equals("Nhan Vien")){
            table ="taxdb.employees";
        }else if(AppController.getInstance().getUser().getRole().equals("Truong phong")){
            table="taxdb.managers";
        }
        System.out.print(table);
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();
        String query = "select fullname from "+table+" where user_id=?";
        PreparedStatement pstm = null;

        Person person = new Person();
        try{
            pstm = conn.prepareStatement(query);
            pstm.setString(1,AppController.getInstance().getUser().getUserId());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                person.setName(rs.getString("fullname"));
            }

            fullName.setText(person.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadView(String idScene, String folder) throws IOException {
        try {
            Parent newScene = FXMLLoader.load(getClass().getResource("/com/example/taxcaculator/view/"+folder+"/" + idScene + ".fxml"));
            newScene.getStylesheets().add(getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm());
            generalScene.getChildren().clear();
            generalScene.getChildren().add(newScene);
            AnchorPane.setTopAnchor(newScene, 0.0);
            AnchorPane.setBottomAnchor(newScene, 0.0);
            AnchorPane.setRightAnchor(newScene, 0.0);
            AnchorPane.setLeftAnchor(newScene, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void HandleButtonOnClick(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String idBtn = clickedButton.getId();
        List<String> classBtn = clickedButton.getStyleClass();

        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selectedBtn");
            selectedButton.getStyleClass().add("primary-btn");
        }

        selectedButton = clickedButton;
        selectedButton.getStyleClass().remove("primary-btn");
        selectedButton.getStyleClass().add("selectedBtn");
        LoadView(idBtn,classBtn.get(1));
    }

    public void ChangePassOnClick(){
        try {
            if (selectedButton != null) {
                selectedButton.getStyleClass().remove("selectedBtn");
                selectedButton.getStyleClass().add("primary-btn");
                selectedButton = null; // Xóa trạng thái nút được chọn
            }
            LoadView("changePass", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void MoveOnNotification(){
        try {
            if (selectedButton != null) {
                selectedButton.getStyleClass().remove("selectedBtn");
                selectedButton.getStyleClass().add("primary-btn");
                selectedButton = null; // Xóa trạng thái nút được chọn
            }
            LoadView("message", "accountant");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void HandleLogout(MouseEvent event){
        try {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận đăng xuất");
            alert.setHeaderText("Bạn có muốn đăng xuất không?");



            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                AppController.getInstance().setUser(null);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taxcaculator/view/login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);


                String css = getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoListDepartment(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taxcaculator/view/accountant/listDepartments.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        phongBan = null;
        String css = getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
