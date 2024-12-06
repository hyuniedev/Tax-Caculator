package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.PhongBan;
import com.example.taxcaculator.model.TruongPhong;
import com.example.taxcaculator.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class addNewDepartmentController {
    @FXML
    private TextField nameDepartment;
    @FXML
    private TextField nameManager;
    @FXML
    private TextField dobManager;
    @FXML
    private TextField dependentManager;
    @FXML
    private TextField phoneManager;
    @FXML
    private TextField emailManager;
    @FXML
    private TextField addressManager;

    @FXML
    private void formatDobInput() {
        String input = dobManager.getText().replaceAll("[^\\d]", ""); // Loại bỏ tất cả ký tự không phải số

        StringBuilder formattedInput = new StringBuilder();

        // Thêm phần năm
        if (input.length() >= 4) {
            formattedInput.append(input.substring(0, 4)).append("-");
        } else if (input.length() > 0) {
            formattedInput.append(input);
        }

        // Thêm phần tháng
        if (input.length() >= 6) {
            formattedInput.append(input.substring(4, 6)).append("-");
        } else if (input.length() > 4) {
            formattedInput.append(input.substring(4));
        }

        // Thêm phần ngày
        if (input.length() >= 8) {
            formattedInput.append(input.substring(6, 8));
        } else if (input.length() > 6) {
            formattedInput.append(input.substring(6));
        }

        // Đảm bảo không vượt quá độ dài tối đa của định dạng yyyy-MM-dd
        if (formattedInput.length() > 10) {
            formattedInput.setLength(10);
        }

        // Cập nhật lại giá trị trong TextField
        dobManager.setText(formattedInput.toString());

        // Đặt con trỏ về cuối
        dobManager.positionCaret(formattedInput.length());

    }

    private boolean isAnyFieldEmpty() {
        if (nameDepartment.getText().isEmpty() || nameManager.getText().isEmpty() ||
                dobManager.getText().isEmpty() || dependentManager.getText().isEmpty() ||
                phoneManager.getText().isEmpty() || emailManager.getText().isEmpty() ||
                addressManager.getText().isEmpty()) {
            showAlert("Lỗi nhập liệu", "Không được để trống bất kỳ thông tin nào.");
            return true; // Có ít nhất một trường rỗng
        }
        return false; // Không có trường nào rỗng
    }
    public boolean isValidDate(String date) {
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            showAlert("Ngày sinh không hợp lệ", "Ngày sinh không hợp lệ hoặc không đúng định dạng yyyy-MM-dd");
            return false;
        }
    }


    public boolean isValidPhone(String phone) {
        if (phone.length() == 10 && phone.matches("\\d{10}")) {
            return true;
        } else {
            showAlert("Số điện thoại không hợp lệ", "Số điện thoại phải chứa 10 chữ số và chỉ chứa số.");
            return false;
        }
    }


    private boolean isNameDepartmentTonTai() {
        ResultSet rs = QueryController.getInstance().Query("select * from taxdb.departments where LOWER(department_name) = '" + nameDepartment.getText().toLowerCase() + "';");
        try {
            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isValidEmail(String email) {
        if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return true;
        } else {
            showAlert("Email không hợp lệ", "Email phải có định dạng hợp lệ.");
            return false;
        }
    }

    public boolean isValidDependents(String dependents) {
        try {
            int dependentsNum = Integer.parseInt(dependents);
            if (dependentsNum >= 0 && dependentsNum <= 10) {
                return true;
            } else {
                showAlert("Số phụ thuộc không hợp lệ", "Số phụ thuộc phải nằm trong khoảng từ 0 đến 10.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Số phụ thuộc không hợp lệ", "Số phụ thuộc phải là một số nguyên.");
            return false;
        }
    }

    public boolean isValidAddress(String address) {
        if (address.length() > 5) {
            return true;
        } else {
            showAlert("Địa chỉ không hợp lệ", "Địa chỉ phải có độ dài lớn hơn 5 ký tự.");
            return false;
        }
    }

    public boolean isValidDepartmentName(String name) {
        if (name.length() > 8) {
            return true;
        } else {
            showAlert("Tên phòng ban không hợp lệ", "Tên phòng ban phải có độ dài lớn hơn 8 ký tự.");
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void HandleAddNewDepartment(ActionEvent event) throws IOException {
        if (isAnyFieldEmpty()) {
            return;
        }

        if(isNameDepartmentTonTai()){
            AlertController.alert(Alert.AlertType.ERROR,"Cảnh báo","Tên phòng ban đã tồn tại!");
            return;
        }

        System.out.println(dobManager.getText());

        String nameDep = nameDepartment.getText();
        String nameMgr = nameManager.getText();
        String dobMgr = dobManager.getText();
        String dependentsMgr = dependentManager.getText();
        String phoneMgr = phoneManager.getText();
        String emailMgr = emailManager.getText();
        String addressMgr = addressManager.getText();

        if (isValidDepartmentName(nameDep) && isValidPhone(phoneMgr) && isValidEmail(emailMgr) &&
                isValidDate(dobMgr) && isValidDependents(dependentsMgr) && isValidAddress(addressMgr)) {
            User user = new User("Truong phong");
            createUser(user.getUserId());
            createManager(user.getUserId());
            createDepartment();

            changeSceneListDepartment(event);
        }
    }

    public void createManager(String userID){

        TruongPhong truongPhong = new TruongPhong();
        truongPhong.setName(nameManager.getText());
        truongPhong.setDob(dobManager.getText());
        truongPhong.setAddress(addressManager.getText());
        truongPhong.setGmail(emailManager.getText());
        truongPhong.setNumberPhone(phoneManager.getText());
        truongPhong.setSoNguoiPhuThuoc(Integer.parseInt(dependentManager.getText()));
        truongPhong.setUid(QueryController.getInstance().getNextID("taxdb.managers","manager_id"));


        String query = "INSERT INTO managers(manager_id,user_id,fullname,hometown,birth,email,phone,dependents)\n" +
                "VALUES('"+truongPhong.getUid()
                +"','"+userID+"','"+truongPhong.getName()
                +"','"+truongPhong.getAddress()+"','"+truongPhong.getDob()
                +"','"+truongPhong.getGmail()+"','"+truongPhong.getNumberPhone()+"','"+truongPhong.getSoNguoiPhuThuoc()+"')";

        QueryController.getInstance().InsertValue(query);
    }

    public void createDepartment(){
        PhongBan phongBan = new PhongBan();
        phongBan.setId(QueryController.getInstance().getNextID("departments", "department_id"));
        phongBan.setTenPhongBan(nameDepartment.getText());
        phongBan.setIdTruongPhong(QueryController.getInstance().getNextID("departments", "manager_id"));
        String query="INSERT INTO departments(department_id, department_name, manager_id, accounting_id)\n" +
                "VALUES('"+phongBan.getId()+"','"+phongBan.getTenPhongBan()+"','"+phongBan.getIdTruongPhong()+"','AC01');";
        QueryController.getInstance().InsertValue(query);
    }

    public void createUser(String userID){
        String query="INSERT INTO users(user_role,user_id,username,password)\n" +
                "VALUES('Truong phong','"+userID+"','"+userID.toLowerCase()+"','"+userID.toLowerCase()+"')";
        QueryController.getInstance().InsertValue(query);

    }


    public void changeSceneListDepartment(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/accountant/listDepartments.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        String css = getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void gotoListDepartment(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taxcaculator/view/accountant/listDepartments.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        String css = getClass().getResource("/com/example/taxcaculator/view/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void formatName(){
        restrictToLettersAndSpaces(nameManager);
    }
    @FXML
    private void formatAddress(){
        restrictToLettersNumbersAndSpaces(addressManager);
    }
    private void restrictToLettersAndSpaces(TextField field) {
        String input = field.getText().replaceAll("[^\\p{L}\\s]", ""); // Loại bỏ tất cả ký tự không phải chữ cái hoặc dấu cách

        // Cập nhật lại giá trị trong TextField
        field.setText(input);

        // Đặt con trỏ về cuối
        field.positionCaret(input.length());
    }
    @FXML
    private void formatNumberPhone(){
        restrictToNumbers(phoneManager);
    }
    @FXML
    private void formatSNPT(){
        restrictToNumbers(dependentManager);
    }
    private void restrictToNumbers(TextField field) {
        String input = field.getText().replaceAll("[^\\d]", ""); // Loại bỏ tất cả ký tự không phải số

        // Cập nhật lại giá trị trong TextField
        field.setText(input);

        // Đặt con trỏ về cuối
        field.positionCaret(input.length());
    }
    @FXML
    private void formatNameDepartment(){
        restrictToLettersNumbersAndSpaces(nameDepartment);
    }

    private void restrictToLettersNumbersAndSpaces(TextField field) {
        String input = field.getText().replaceAll("[^\\p{L}\\p{N}\\s]", ""); // Loại bỏ tất cả ký tự không phải chữ cái, số hoặc dấu cách

        // Cập nhật lại giá trị trong TextField
        field.setText(input);

        // Đặt con trỏ về cuối
        field.positionCaret(input.length());
    }
}
