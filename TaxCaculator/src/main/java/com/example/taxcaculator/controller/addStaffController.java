package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.NhanVien;
import com.example.taxcaculator.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.time.DateTimeException;
import java.time.LocalDate;

public class addStaffController {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDob;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtNumberPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtSoNguoiPhuThuoc;

    @FXML
    private Button addNewStaff;

    public void initialize(){
        addEvent();
    }

    private void resetTextFields(){
        txtName.clear();
        txtDob.clear();
        txtAddress.clear();
        txtNumberPhone.clear();
        txtEmail.clear();
        txtSoNguoiPhuThuoc.clear();
    }

    private void addEvent() {
        addNewStaff.setOnAction(actionEvent -> {
            if(txtName.getText().isEmpty()){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Tên không được bỏ trống!");
                return;
            }
            if(txtName.getText().length() < 5){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Tên phải lớn hơn 4 ký tự! Vui lòng nhập đầy đủ Họ và Tên!");
                return;
            }
            if(txtNumberPhone.getText().isEmpty()){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Số điện thoại không được bỏ trống!");
                return;
            }
            else if(txtNumberPhone.getText().length() != 10){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Vui lòng nhập điện thoại 10 số!");
                return;
            }
            if(txtDob.getText().isEmpty() || txtDob.getText().length()!=10){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Ngày tháng năm sinh đang bỏ trống hoặc nhập không đủ!");
                return;
            }

            if(!isValidDate(txtDob.getText())){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Ngày tháng năm sinh không hợp lệ hoặc đúng định dạng yyyy-MM-dd");
                return;
            }

            if(txtEmail.getText().isEmpty()){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Gmail không được bỏ trống!");
                return;
            }
            if(!isValidEmail(txtEmail.getText())){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Gmail không đúng định dạng!");
                return;
            }
            if(txtSoNguoiPhuThuoc.getText().isEmpty()){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Số người phụ thuộc không được bỏ trống!");
                return;
            }
            if(Integer.parseInt(txtSoNguoiPhuThuoc.getText())<0 || Integer.parseInt(txtSoNguoiPhuThuoc.getText())>10){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Số người phụ thuộc phải trong khoảng 0 - 10!");
                return;
            }
            if(txtAddress.getText().isEmpty()){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Địa chỉ không được bỏ trống!");
                return;
            }
            if(txtAddress.getText().length() < 5){
                AlertController.alert(Alert.AlertType.ERROR,"Error","Địa chỉ phải lớn hơn 4 ký tự!");
                return;
            }
            NhanVien nv = new NhanVien();
            nv.setName(txtName.getText());
            nv.setDob(txtDob.getText());
            nv.setAddress(txtAddress.getText());
            nv.setGmail(txtEmail.getText());
            nv.setNumberPhone(txtNumberPhone.getText());
            nv.setSoNguoiPhuThuoc(Integer.parseInt(txtSoNguoiPhuThuoc.getText()));
            nv.setIdPhongBan(navBarController.phongBan.getId());
            nv.setUid(QueryController.getInstance().getNextID("taxdb.employees","employee_id"));

            User user = new User("Nhan Vien");

            QueryController.getInstance().InsertValue("Insert into taxdb.users (user_role, user_id, username, password) Values ('Nhan Vien','"+user.getUserId()+"','"+user.getUserId().toLowerCase()+"','"+user.getUserId().toLowerCase()+"');");
            QueryController.getInstance().InsertValue("Insert into taxdb.employees (employee_id, user_id, department_id, fullname, phone, email, birth, hometown, dependents) Values ('"+nv.getUid()+"','"+user.getUserId()+"','"+navBarController.phongBan.getId()+"','"+nv.getName()+"','"+nv.getNumberPhone()+"','"+nv.getGmail()+"','"+nv.getDob()+"','"+nv.getAddress()+"',"+nv.getSoNguoiPhuThuoc()+");");
            AlertController.alert(Alert.AlertType.INFORMATION,"Thông báo","Đã thêm thành công!");
            resetTextFields();
        });
    }
    public static boolean isValidDate(String dob) {
        try {
            // Tạo đối tượng LocalDate từ ngày, tháng, năm
            LocalDate.parse(dob);
            return true; // Nếu không có lỗi, ngày hợp lệ
        } catch (DateTimeException e) {
            // Nếu xảy ra lỗi (ngày không hợp lệ), trả về false
            return false;
        }
    }
    public static boolean isValidEmail(String email) {
        // Biểu thức chính quy kiểm tra email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        // Kiểm tra nếu email null hoặc không khớp regex
        if (email == null || !email.matches(emailRegex)) {
            return false; // Email không hợp lệ
        }
        return true; // Email hợp lệ
    }

    @FXML
    private void formatNumberPhone(){
        restrictToNumbers(txtNumberPhone);
    }

    @FXML
    private void formatSNPT(){
        restrictToNumbers(txtSoNguoiPhuThuoc);
    }

    private void restrictToNumbers(TextField field) {
        String input = field.getText().replaceAll("[^\\d]", ""); // Loại bỏ tất cả ký tự không phải số

        // Cập nhật lại giá trị trong TextField
        field.setText(input);

        // Đặt con trỏ về cuối
        field.positionCaret(input.length());
    }

    @FXML
    private void formatDobInput() {
        String input = txtDob.getText().replaceAll("[^\\d]", ""); // Loại bỏ tất cả ký tự không phải số

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
        txtDob.setText(formattedInput.toString());

        // Đặt con trỏ về cuối
        txtDob.positionCaret(formattedInput.length());

    }
    @FXML
    private void restrictToLettersAndSpaces() {
        String input = txtName.getText().replaceAll("[^\\p{L}\\p{N}\\s]", ""); // Loại bỏ tất cả ký tự không phải chữ cái hoặc dấu cách

        // Cập nhật lại giá trị trong TextField
        txtName.setText(input);

        // Đặt con trỏ về cuối
        txtName.positionCaret(input.length());
    }
}
