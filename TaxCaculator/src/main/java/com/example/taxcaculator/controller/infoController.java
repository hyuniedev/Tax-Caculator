package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class infoController {
    @FXML
    private Spinner<Integer> dependent;
    @FXML
    private Button submitBtn;
    @FXML
    private Text fullName;
    @FXML
    private Text birthDay;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextField address;
    @FXML
    private Text changeDependentTxt;

    private int currentDependents = 0;
    private  int tempDependent = 0;


    public void initialize() {

        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            // Kiểm tra xem thay đổi có phải là số
            if (change.getText().matches("[0-9]*")) {
                return change; // Nếu là số, cho phép thay đổi
            }
            return null; // Nếu không phải là số, từ chối thay đổi
        });

        // Áp dụng TextFormatter cho TextField phone
        phone.setTextFormatter(phoneFormatter);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0, 1);
        dependent.setValueFactory(valueFactory);
        getInfo();
        readNoti();

    }
    public  void EditOnClick(){
        changeDependentTxt.setText("");
        phone.setDisable(false);
        dependent.setDisable(false);
        email.setDisable(false);
        address.setDisable(false);
        submitBtn.setDisable(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public String validateFields() {
        String phoneText = phone.getText().trim();
        String emailText = email.getText().trim();
        String addressText = address.getText();
        int dependentValue = dependent.getValue();

        // Kiểm tra các trường trống
        if (phoneText.isEmpty() || emailText.isEmpty() || addressText.isEmpty() || dependentValue <= 0) {
            return "Vui lòng điền đầy đủ các ô.";
        }

        // Kiểm tra định dạng email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!emailText.matches(emailRegex)) {
            return "Email không hợp lệ.";
        }

        // Kiểm tra số điện thoại
        String phoneRegex = "\\d{10}";
        if (!phoneText.matches(phoneRegex)) {
            return "Số điện thoại phải là số và có đúng 10 chữ số.";
        }

        // Nếu không có lỗi
        return null;
    }

    public void HandleSubmit(ActionEvent event){
        String validationMessage = validateFields();

        if(isExisted()) return;
        if (validationMessage != null) {
            // Hiển thị thông báo lỗi
            showAlert("Lỗi", validationMessage);
            return;
        }
        checkHasChangeDependents();
        editInfo();
        email.setDisable(true);
        dependent.setDisable(true);
        phone.setDisable(true);
        address.setDisable(true);
        getInfo();
    }

    public void getInfo(){
        String table ="";
        if(AppController.getInstance().getUser().getRole().equals("Nhan Vien")){
            table ="taxdb.employees";
        }else if(AppController.getInstance().getUser().getRole().equals("Truong phong")){
            table="taxdb.managers";
        }
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "select fullname, phone, email, birth, hometown, dependents from " + table +" where user_id=?";
        PreparedStatement pstm = null;
        Person person = new Person();
        try {
            pstm = conn.prepareStatement(query);
            pstm.setString(1, AppController.getInstance().getUser().getUserId());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                person.setName(rs.getString("fullname"));
                person.setNumberPhone(rs.getString("phone"));
                person.setGmail(rs.getString("email"));
                person.setDob(rs.getString("birth"));
                person.setAddress(rs.getString("hometown"));
                person.setSoNguoiPhuThuoc(rs.getInt("dependents"));
                currentDependents = rs.getInt("dependents");
            }
            fullName.setText(person.getName());
            birthDay.setText(person.getDob());
            phone.setText(person.getNumberPhone());
            email.setText(person.getGmail());
            address.setText(person.getAddress());
            int displayDependent = readNoti()!=0 ? tempDependent : currentDependents;
            changeDependentTxt.setText(readNoti()!=0?"*Đang chờ phê duyệt":"");
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, displayDependent, 1);
            dependent.setValueFactory(valueFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTableOfRole(){
        return AppController.getInstance().getUser().getRole().equals("Nhan Vien")?"taxdb.employees":"taxdb.managers";
    }

    public void editInfo(){

        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "UPDATE "+getTableOfRole()+ "\n" +
                "SET phone=?, email =?, hometown=?\n" +
                "WHERE user_id=?;";

//        System.out.println(query);
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(query);
            pstm.setString(1, phone.getText());
            pstm.setString(2,email.getText());
            pstm.setString(3, address.getText());
            pstm.setString(4,AppController.getInstance().getUser().getUserId());

            int row = pstm.executeUpdate();
            if(row != 0){
//                System.out.println("Cập nhật thành công " + row);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean checkHasChangeDependents(){

        System.out.println("currDep: " + currentDependents);

        int newDependent = dependent.getValue();
        System.out.println("newDep1 : "+ newDependent);
        if (currentDependents == newDependent) {
            deleteNoti();
            getInfo();
            return false;
        } else {
            createNoti(newDependent);

        }
        System.out.println("newDep2 : "+ newDependent);
        return true;
    }

    public void createNoti(int newDependents){
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String checkQuery = "SELECT COUNT(*) FROM notifications WHERE "+getNameid()+" = ?";
        String updateQuery = "UPDATE notifications SET accounting_id = 'AC01', change_dependents = ? WHERE "+getNameid()+" = ?";
        String insertQuery = "INSERT INTO notifications("+getNameid()+", accounting_id, change_dependents) VALUES (?, 'AC01', ?)";

        PreparedStatement pstmCheck = null;
        PreparedStatement pstmUpdate = null;
        PreparedStatement pstmInsert = null;

        try {
            // Kiểm tra xem employee_id đã tồn tại hay chưa
            pstmCheck = conn.prepareStatement(checkQuery);
            pstmCheck.setString(1, getPersonId());
            ResultSet rs = pstmCheck.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1); // Lấy số lượng dòng khớp với `employee_id`
            }

            if (count > 0) {
                // Thực hiện UPDATE nếu đã tồn tại
                pstmUpdate = conn.prepareStatement(updateQuery);
                pstmUpdate.setInt(1, newDependents);
                pstmUpdate.setString(2, getPersonId());
                int rowsUpdated = pstmUpdate.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Cập nhật thành công: " + rowsUpdated);
                }
            } else {
                // Thực hiện INSERT nếu chưa tồn tại
                pstmInsert = conn.prepareStatement(insertQuery);
                pstmInsert.setString(1, getPersonId());
                pstmInsert.setInt(2, newDependents);
                int rowsInserted = pstmInsert.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Thêm thành công: " + rowsInserted);
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int readNoti(){
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "select change_dependents from notifications where "+getNameid()+" = '"+getPersonId()+"'";
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()){
                tempDependent = rs.getInt("change_dependents");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempDependent;
    }

    public void deleteNoti(){
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();
        String query="DELETE FROM notifications \n" +
                "WHERE "+getNameid()+" = '"+getPersonId()+"'";
        Statement stm = null;
        try {
            stm = conn.createStatement();
            int row = stm.executeUpdate(query);
            if(row != 0){
                System.out.println("Xóa thành công " + row);
            }
            tempDependent = 0;
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNameid(){
        return AppController.getInstance().getUser().getRole().equals("Nhan Vien")?"employee_id":"manager_id";
    }
    public String getPersonId(){

        String personId="";
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();
        String query= "select "+getNameid()+ " from "+getTableOfRole() + " where user_id='" + AppController.getInstance().getUser().getUserId()+ "'";
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()){
                personId = rs.getString(getNameid());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return personId;
    }

    public boolean isExisted(){
        String query = "select phone, email from "+getTableOfRole()+" where user_id!='"+AppController.getInstance().getUser().getUserId()+"'";
        System.out.println(query);
        ResultSet rs = QueryController.getInstance().Query(query);
        try {
            while (rs.next()){
                if(phone.getText().equals(rs.getString("phone"))){
                    showAlert("","Số đện thoại này đã tồn tại");
                    getInfo();
                    return true;
                }
                if (email.getText().equals(rs.getString("email"))){
                    showAlert("","email này đã tồn tại");
                    getInfo();
                    return true ;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



}
