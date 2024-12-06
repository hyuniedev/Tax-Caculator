package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.TaxCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class tryCacuTaxController {
    @FXML
    private Text giamTruCaNhan;
    @FXML
    private Text giamTruNPT;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField dependentField;
    @FXML
    private Text totalGiamtru;
    @FXML
    private Text taxTry;

    private int gtCaNhan;
    private int gtNPT;

    public void initialize() {
        getGTru();
        TextFormatter<String> dependentFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
        dependentField.setTextFormatter(dependentFormatter);

    }

    public String formatNumber(long number) {
        if (number < 0) return "0";
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(number);
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


    @FXML
    public void formatSalaryField() {
        formatMoneyInput(salaryField);
    }


    public void OnclickTryCacuBtn() {
        if(dependentField.getText().isEmpty()){
            taxTry.setText("Vui lòng nhập số hợp lệ.");
            return;
        }
        try {
            // Lấy dữ liệu từ các trường nhập liệu
            String salaryText = salaryField.getText().replace(".", "").trim();
            int salary = Integer.parseInt(salaryText);

            int dependent = 0;
            if (!dependentField.getText().isEmpty()) {
                String dependentText = dependentField.getText().replace(".", "").trim();
                dependent = Integer.parseInt(dependentText);
            }


            TaxCalculator taxCalculator = new TaxCalculator();
            int tax = (int)taxCalculator.taxMonthly(salary,dependent,LocalDate.now().getYear());
            taxTry.setText(formatNumber(tax) + " đ");

            // Tính tổng giảm trừ
            int total = gtCaNhan + gtNPT * dependent;
            totalGiamtru.setText("Giảm trừ/ tháng: " + formatNumber(total) + " đ");

        } catch (NumberFormatException e) {
            taxTry.setText("Vui lòng nhập số hợp lệ.");
        }
    }



    public void getGTru() {
        int currentYear = LocalDate.now().getYear();
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "SELECT * FROM taxdb.deductions WHERE year_apply='" + currentYear + "'";
        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                gtCaNhan = rs.getInt("self_fee");
                gtNPT = rs.getInt("dependents_fee");
            }
            giamTruCaNhan.setText("*Giảm trừ thu nhập cá nhân/ tháng: " + formatNumber(gtCaNhan) + " đ");
            giamTruNPT.setText("*Giảm trừ người phụ thuộc: " + formatNumber(gtNPT) + " đ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
