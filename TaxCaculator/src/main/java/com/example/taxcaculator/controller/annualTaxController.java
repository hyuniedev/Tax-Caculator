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

public class annualTaxController {
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

        String queryE= "SELECT \n" +
                "    e.fullname, \n" +
                "    e.dependents, \n" +
                "    SUM(p.salary) AS total_salary, \n" +
                "    SUM(p.tax) AS total_tax, \n" +
                "    SUM(d.dependents_fee * e.dependents + d.self_fee) AS total_deductions, " +
                " COUNT(DISTINCT MONTH(p.time_pay)) AS total_months " +
                "FROM \n" +
                "    employees e \n" +
                "LEFT JOIN \n" +
                "    payroll p \n" +
                "    ON e.employee_id = p.employee_id \n" +
                "JOIN \n" +
                "    deductions d \n" +
                "    ON d.year_apply = " + year+
                " WHERE \n" +
                "    e.department_id = '"+navBarController.phongBan.getId()+"' AND YEAR(p.time_pay) = "+year +"\nGROUP BY \n" +
                "    e.employee_id, e.fullname, e.dependents;" ;


        String queryM = "SELECT \n" +
                "    m.fullname, \n" +
                "    m.dependents, \n" +
                "    SUM(p.salary) AS total_salary, \n" +
                "    SUM(p.tax) AS total_tax, \n" +
                "    SUM(d.dependents_fee * m.dependents + d.self_fee) AS total_deductions, " +
                "    COUNT(DISTINCT MONTH(p.time_pay)) AS total_months \n" +
                "FROM \n" +
                "    managers m \n" +
                "LEFT JOIN \n" +
                "    payroll p \n" +
                "    ON m.manager_id = p.manager_id \n" +
                "JOIN \n" +
                "    deductions d \n" +
                "    ON d.year_apply = " + year + " \n" +
                "JOIN \n" +
                "    departments de \n" +  // Đảm bảo có dấu cách giữa 'departments de' và phần sau
                "    ON de.manager_id = m.manager_id \n" +  // Đảm bảo rằng dấu cách có sau 'ON'
                "WHERE \n" +
                "    de.department_id = '" + navBarController.phongBan.getId() + "' AND YEAR(p.time_pay) = " + year + " \n" +
                "GROUP BY \n" +
                "    m.manager_id, m.fullname, m.dependents;";
        listSalaryStaffAnnual.clear();


        try {
            ResultSet rs = QueryController.getInstance().Query(queryE);
            while (rs.next()){
                SalaryData salaryData1 = new SalaryData();
                totalMonth = rs.getInt("total_months");
                TaxCalculator taxCalculator = new TaxCalculator();
                salaryData1.setName(rs.getString("e.fullname"));
                salaryData1.setSalary(rs.getInt("total_salary"));
                salaryData1.setTotalDeductions(rs.getInt("total_deductions"));
                salaryData1.setTax(rs.getInt("total_tax"));
                salaryData1.setTaxYear((int)taxCalculator.taxAnnual(salaryData1.getSalary(), rs.getInt("e.dependents"),year));
                salaryData1.setRsTax(getRsTax(salaryData1.getSalary(),rs.getInt("e.dependents"),year,salaryData1.getTax()));

                listSalaryStaffAnnual.add(salaryData1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ResultSet rs = QueryController.getInstance().Query(queryM);
            while (rs.next()){
                SalaryData salaryData2 = new SalaryData();
                totalMonth = rs.getInt("total_months");
                TaxCalculator taxCalculator = new TaxCalculator();
                salaryData2.setName(rs.getString("m.fullname"));
                salaryData2.setSalary(rs.getInt("total_salary"));
                salaryData2.setTotalDeductions(rs.getInt("total_deductions"));
                salaryData2.setTax(rs.getInt("total_tax"));
                salaryData2.setTaxYear((int)taxCalculator.taxAnnual(salaryData2.getSalary(), rs.getInt("m.dependents"),year));
                salaryData2.setRsTax(getRsTax(salaryData2.getSalary(),rs.getInt("m.dependents"),year,salaryData2.getTax()));

                listSalaryStaffAnnual.add(salaryData2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
