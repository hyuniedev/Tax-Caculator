package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.SalaryData;
import com.example.taxcaculator.model.TaxCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class annualSettlementController {
    @FXML
    ComboBox<Integer> chooseYear;
    @FXML
    TableView<SalaryData> listStaffAnnual;
    @FXML
    private TableColumn<SalaryData, Integer> sttColumn;
    @FXML
    private TableColumn<SalaryData, String> nameStaffColumn;
    @FXML
    private TableColumn<SalaryData, Integer> totalSalaryColumn;
    @FXML
    private TableColumn<SalaryData, Integer> totalFeeColumn;
    @FXML
    private TableColumn<SalaryData, Integer> totalTaxColumn;
    @FXML
    private TableColumn<SalaryData, Integer> taxYearColumn;
    @FXML
    private TableColumn<SalaryData,String> rsColumn;
    private ObservableList<SalaryData> listSalaryStaffAnnual = FXCollections.observableArrayList();

    private int totalMonth=0;

    public void initialize(){
        ViewChooseYear();
        setupTableColumns();
        getListStaffAnnual(LocalDate.now().getYear());
    }

    public String formatNumber(int number) {
        if (number < 0) return "0";
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(number);
    }
    public void ViewChooseYear(){
        int currentYear = LocalDate.now().getYear();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for(int i = 2022 ; i <= currentYear; i++){
            years.add(i);
        }
        chooseYear.setItems(years);
        chooseYear.setValue(currentYear);
        chooseYear.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    setStyle("-fx-text-fill: white;");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white;");
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
                    setStyle("-fx-text-fill: #3382FE;");
                }
            }
        });
        chooseYear.setOnAction(event -> getListStaffAnnual(getYearSelected()));
    }

    public int getYearSelected(){
        return chooseYear.getValue();
    }
    public void setupTableColumns(){
        nameStaffColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        totalFeeColumn.setCellValueFactory(new PropertyValueFactory<>("totalDeductions"));
        totalTaxColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        taxYearColumn.setCellValueFactory(new PropertyValueFactory<>("taxYear"));
        rsColumn.setCellValueFactory(new PropertyValueFactory<>("rsTax"));

        sttColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() >= listSalaryStaffAnnual.size()) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });
        totalSalaryColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item)); // Định dạng số thành chuỗi
                }
            }
        });
        totalFeeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item)); // Định dạng số thành chuỗi
                }
            }
        });
        totalTaxColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item)); // Định dạng số thành chuỗi
                }
            }
        });
        taxYearColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatNumber(item)); // Định dạng số thành chuỗi
                }
            }
        });
        listStaffAnnual.setItems(listSalaryStaffAnnual);

    }

    public void getListStaffAnnual(int year){
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query= "SELECT \n" +
                "    e.fullname, \n" +
                "    e.dependents, \n" +
                "    p.employee_id, \n" +
                "    SUM(p.salary) AS total_salary, \n" +
                "    SUM(p.tax) AS total_tax, \n" +
                "    SUM(d.dependents_fee * e.dependents + d.self_fee) AS total_deductions, " +
                " COUNT(DISTINCT MONTH(p.time_pay)) AS total_months " +
                "FROM \n" +
                "    employees e \n" +
                "JOIN \n" +
                "    payroll p \n" +
                "    ON e.employee_id = p.employee_id \n" +
                "JOIN \n" +
                "    deductions d \n" +
                "    ON d.year_apply = " + year+
                " WHERE \n" +
                "    e.department_id = '"+getIdDepartment()+"' AND YEAR(p.time_pay) = "+year +"\nGROUP BY \n" +
                "    e.employee_id, e.fullname, e.dependents;" ;


        listSalaryStaffAnnual.clear();

        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);


            while (rs.next()){
                SalaryData salaryData = new SalaryData();
                totalMonth = rs.getInt("total_months");
                TaxCalculator taxCalculator = new TaxCalculator();
                salaryData.setName(rs.getString("e.fullname"));
                salaryData.setSalary(rs.getInt("total_salary"));
                salaryData.setTotalDeductions(rs.getInt("total_deductions"));
                salaryData.setTax(rs.getInt("total_tax"));
                salaryData.setTaxYear((int)taxCalculator.taxAnnual(salaryData.getSalary(), rs.getInt("e.dependents"),year));
                salaryData.setRsTax(getRsTax(salaryData.getSalary(),rs.getInt("e.dependents"),year,salaryData.getTax()));

                listSalaryStaffAnnual.add(salaryData);
            }
            listStaffAnnual.setItems(listSalaryStaffAnnual);
            listStaffAnnual.refresh();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIdDepartment(){
        String idDep="";
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();

        String query = "select d.department_id from departments d join managers m on d.manager_id = m.manager_id where user_id='"+ AppController.getInstance().getUser().getUserId()+"'";

        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs =stm.executeQuery(query);
            while (rs.next()){
                idDep = rs.getString("d.department_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return idDep;
    }

    public String getRsTax(int salary, int dependents, int year, int taxMonth){
        String rs="";
        if(totalMonth == 12){
            TaxCalculator taxCalculator = new TaxCalculator();
            double taxAnnual = taxCalculator.taxAnnual(salary,dependents,year);
            int result = 0;
            if(taxAnnual == taxMonth){
                rs="0 đ";
            }
            else if(taxAnnual > taxMonth) {
                result = (int)taxAnnual - taxMonth;
                rs= " + "+formatNumber(result) + " đ";
            }else {
                result = taxMonth- (int)taxAnnual ;
                rs= " - "+formatNumber(result) + " đ";
            }
        }else {
            rs =" Chưa đủ 12 tháng";
        }



        return rs;
    }
}
