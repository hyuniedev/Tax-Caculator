package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.SalaryData;
import com.example.taxcaculator.model.TaxCalculator;
import com.example.taxcaculator.model.YearFee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class salaryMonthController {
    @FXML
    private Text giamTruCaNhan;
    @FXML
    private Text giamTruNPT;
    private int gtCaNhan;
    private int gtNPT;

    @FXML
    private ComboBox<Integer> chooseYear;
    @FXML
    private TableView<SalaryData> salaryTable;

    @FXML
    private TableColumn<SalaryData, String> monthColumn;

    @FXML
    private TableColumn<SalaryData, Integer> salaryColumn;

    @FXML
    private TableColumn<SalaryData, Integer> dependentsColumn;

    @FXML
    private TableColumn<SalaryData, Integer> totalDeductionsColumn;

    @FXML
    private TableColumn<SalaryData, Integer> taxColumn;

    private ObservableList<SalaryData> salaryList = FXCollections.observableArrayList();

    private int dependents=0;
    @FXML
    private Text totalSalary, totalDependentFee, totalSelfFee, totalTax, taxYear, rsTax;
    private  int totalSalaryMonth=0, totalDependent=0, totalSelf=0, totalTaxMonth =0;
    private int  dependentFee,selfFee;

    private int sa, tax;


    @FXML
    public void initialize() {

        ViewComboYear();
        setupTableColumns();
        getSalaryOfPerson(LocalDate.now().getYear());
        getGTru();
    }
    public String formatNumber(int number) {
        if (number < 0) return "0";
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(number);
    }
    private void setupTableColumns() {

        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        taxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        dependentsColumn.setCellValueFactory(new PropertyValueFactory<>("dependents"));
        totalDeductionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalDeductions"));

        salaryColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item));
                }
            }
        });

        taxColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item));
                }
            }
        });

        totalDeductionsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item)); // Gọi phương thức formatNumber
                }
            }
        });

        // Gán danh sách vào TableView
        salaryTable.setItems(salaryList);
    }

    public void ViewComboYear(){
        int currentYear = LocalDate.now().getYear();


        ObservableList<Integer> years = FXCollections.observableArrayList();


        for (int i = 2022; i <= currentYear; i++) {
            years.add(i);
        }

        // Gán danh sách các năm vào ComboBox
        chooseYear.setItems(years);
        chooseYear.setValue(currentYear); // Đặt giá trị mặc định là năm hiện tại

        // Tùy chỉnh màu sắc cho promptText
        chooseYear.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    setStyle("-fx-text-fill: white;"); // Màu trắng cho promptText
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white;"); //
                }
            }
        });

        // Tùy chỉnh màu sắc cho các mục trong danh sách
        chooseYear.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #3382FE;"); // Màu xanh cho các mục trong danh sách
                }
            }
        });
        chooseYear.setOnAction(event -> {
            getSalaryOfPerson(getSelectedYear());
            getGTru();
        });


    }
    public int getSelectedYear(){
        System.out.println("year: "+ chooseYear.getValue());
        return chooseYear.getValue();
    }

    public void getSalaryOfPerson(int year){
        salaryList.clear();
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query="SELECT month(time_pay),salary, tax, time_pay FROM taxdb.payroll where "+checkRole()+"='" +getID()+ "' and year(time_pay) = " + year +" order by month(time_pay) asc";

        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()){
                SalaryData salaryData = new SalaryData();
                salaryData.setSalary(rs.getInt("salary"));
                salaryData.setTax(rs.getInt("tax"));
                salaryData.setMonth("    Tháng " + rs.getString("month(time_pay)"));
                salaryData.setDependents(dependents);
                salaryData.setTotalDeductions(calculateDeductions(year));


                totalSalaryMonth+= salaryData.getSalary();
                totalDependent+= dependents * dependentFee;

                totalSelf+= selfFee;
                totalTaxMonth+=salaryData.getTax();


                salaryList.add(salaryData);
            }
            totalSalary.setText(formatNumber(totalSalaryMonth) + " đ");
            totalDependentFee.setText(formatNumber(totalDependent) + " đ");
            totalSelfFee.setText(formatNumber(totalSelf)+ " đ");
            totalTax.setText(formatNumber(totalTaxMonth)+ " đ");
            sa = totalSalaryMonth;
            tax = totalTaxMonth;
            System.out.println("sa: "+ sa + ", tax: "+ tax);
            totalSalaryMonth=0; totalDependent=0; totalSelf=0; totalTaxMonth =0;

            salaryTable.setItems(salaryList);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public String getID(){
        String id="";
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();
        String query= "SELECT dependents, " +checkRole()+ " FROM " +getTable()+" where user_id = " +"'" + AppController.getInstance().getUser().getUserId()+"'" ;

        System.out.println(query);
        Statement stm =null;
        try{
            stm = conn.createStatement();
            ResultSet rs= stm.executeQuery(query);
            while (rs.next()){
                id = rs.getString(checkRole());
                dependents = rs.getInt("dependents");
            }
            System.out.println(dependents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    public String checkRole(){
        String personID="";
        if(AppController.getInstance().getUser().getRole().equals("Nhan Vien")){
            personID ="employee_id";
        }else if(AppController.getInstance().getUser().getRole().equals("Truong phong")){
            personID ="manager_id";
        }
        return personID;
    }
    public String getTable(){
        String table="";
        if(AppController.getInstance().getUser().getRole().equals("Nhan Vien")){
            table ="taxdb.employees";
        }else if(AppController.getInstance().getUser().getRole().equals("Truong phong")){
            table ="taxdb.managers";
        }
        return table;
    }
    private int calculateDeductions(int year) {
        YearFee.getFee(year);
        selfFee = YearFee.getSelfFee();
        dependentFee = YearFee.getDependentFee();
        int totalFee = selfFee + dependents*dependentFee;
        return totalFee;
    }


    public void HandleSettlementBtn(ActionEvent event) {
        int year = getSelectedYear();
        CaCuTaxAnnual(year);

    }
    public void CaCuTaxAnnual(int year){
        if(salaryList.size()==12){
            TaxCalculator taxCalculator = new TaxCalculator();
            double taxAnnual = taxCalculator.taxAnnual(sa,dependents,year);
            System.out.println(taxAnnual);
            taxYear.setText(formatNumber((int)taxAnnual));
            int result = 0;
            if(taxAnnual == tax){
                rsTax.setText("0 đ");
            }
            else if(taxAnnual > tax) {
                result = (int)taxAnnual - tax;
                rsTax.setText(" + "+formatNumber(result) + " đ");
            }else {
                result = tax- (int)taxAnnual ;
                rsTax.setText(" - "+formatNumber(result) + " đ");
            }
        }else {
            taxYear.setText("Chưa đủ 12 tháng lương");
            rsTax.setText("Chưa đủ 12 tháng lương");
        }


    }
    public void getGTru() {
        int currentYear = LocalDate.now().getYear();
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "SELECT * FROM taxdb.deductions WHERE year_apply=" + getSelectedYear() ;
        System.out.println(query);
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
