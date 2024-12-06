package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.Money;
import com.example.taxcaculator.model.PhongBan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class simpleDepartmentController {
    @FXML
    private AnchorPane lsButton;
    @FXML
    private TextField txtMucGiamTruNguoiPhuThuoc;
    @FXML
    private TextField txtMucGiamTruCaNhan;
    @FXML
    private Text txtTitleMucGiamTru;
    @FXML
    private Button updateBtn;

    public void initialize() throws SQLException {
        LoadDepartments();
        LoadMucGiamTru();
        addActionToButton();
    }

    private void addActionToButton(){
        updateBtn.setOnAction(event -> {
            if(!txtMucGiamTruNguoiPhuThuoc.getText().isEmpty() && !txtMucGiamTruCaNhan.getText().isEmpty()){
                if(AlertController.alert(Alert.AlertType.INFORMATION,"Thông báo","Xác nhận thay đổi?")){
                    QueryController.getInstance().InsertValue("Update taxdb.deductions set dependents_fee = " + Money.unFormat(txtMucGiamTruNguoiPhuThuoc.getText())+", self_fee = " + Money.unFormat(txtMucGiamTruCaNhan.getText())+" where year_apply = " + LocalDate.now().getYear()+";");
                }
            }else{
                AlertController.alert(Alert.AlertType.ERROR,"Cảnh báo","Không được bỏ trống mức giảm trừ cá nhân và mức giảm trừ người phụ thuộc!");
            }
        });
    }

    public void LoadView(ActionEvent event) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taxcaculator/view/accountant/general.fxml"));
            Parent root = loader.load();

            navBarController controller = loader.getController();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void LoadMucGiamTru() throws SQLException {
        int curYear = LocalDate.now().getYear();
        txtTitleMucGiamTru.setText("Mức giảm trừ "+curYear);
        ResultSet rs = QueryController.getInstance().Query("select dependents_fee, self_fee from taxdb.deductions where year_apply = "+curYear +";");
        if (rs.next()){
            txtMucGiamTruCaNhan.setText(Money.format(rs.getString("self_fee")));
            txtMucGiamTruNguoiPhuThuoc.setText(Money.format(rs.getString("dependents_fee")));
        }
    }


    @FXML
    private void handleMucGiamTruCaNhanAction() {
        formatMoneyInput(txtMucGiamTruCaNhan);
    }

    @FXML
    private void handleMucGiamTruNguoiPhuThuocAction() {
        formatMoneyInput(txtMucGiamTruNguoiPhuThuoc);
    }

    private void formatMoneyInput(TextField txtMoney){
        String input = txtMoney.getText().replaceAll("[^\\d]", ""); // Loại bỏ tất cả ký tự không phải số

        StringBuilder formattedInput = new StringBuilder();

        int length = input.length();

        // Duyệt qua chuỗi từ phải sang trái và thêm dấu chấm sau mỗi 3 chữ số
        for (int i = 0; i < length; i++) {
            if (i > 0 && i % 3 == 0) {
                formattedInput.insert(0, "."); // Thêm dấu chấm vào đầu chuỗi
            }
            formattedInput.insert(0, input.charAt(length - 1 - i)); // Thêm từng ký tự từ cuối chuỗi
        }

        // Cập nhật giá trị trong TextField
        txtMoney.setText(formattedInput.toString());

        // Đặt con trỏ về cuối
        txtMoney.positionCaret(formattedInput.length());
    }

    private void LoadDepartments() throws SQLException {
        int i = 0;
        ResultSet rs = QueryController.getInstance().Query("select department_id,department_name,manager_id from taxdb.departments");
        while(rs.next()){
            PhongBan phongBan = new PhongBan(rs.getString("department_id"),rs.getString("department_name"),rs.getString("manager_id"));
            lsButton.getChildren().add(createDepartmentButton(phongBan,i));
            i++;
        }

    }

    public Button createDepartmentButton(PhongBan phongBan, int index) {
        double layoutY = calculateLayoutY(index);

        Button button = new Button(phongBan.getTenPhongBan());
        button.setPrefWidth(468.0);
        button.setPrefHeight(128.0);
        button.setLayoutX(52.0);
        button.setLayoutY(layoutY);
        button.getStyleClass().add("department");
        button.setFont(Font.font("Arial Bold", 32.0));

        button.setOnAction(e -> {
            navBarController.phongBan = phongBan;
            try {
                LoadView(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return button;
    }


    public double calculateLayoutY(int index) {
        return 50 + (index * 183.0);
    }

    public void callSceneAddNewDepartment(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taxcaculator/view/accountant/addNewDepartment.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        String css = getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
