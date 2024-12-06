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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class listStaffOfAccountant {
    @FXML
    private ComboBox<Integer> chooseYear;
    @FXML
    private ComboBox<Integer> chooseMonth;

    @FXML
    private TableView<SalaryData> listStaff;
    @FXML
    private TableColumn<SalaryData, Integer> sttColumn;
    @FXML
    private TableColumn<SalaryData, String> nameStaffColumn;
    @FXML
    private TableColumn<SalaryData, Integer> salaryColumn;
    @FXML
    private TableColumn<SalaryData, Integer> dependentsColumn;
    @FXML
    private TableColumn<SalaryData, Integer> totalDeductionsColumn;
    @FXML
    private TableColumn<SalaryData, Integer> taxMonthColumn;
    @FXML
    private TableColumn<SalaryData, String> phoneColumn;
    @FXML
    private TableColumn<SalaryData, String> emailColumn;
    @FXML
    private TableColumn<SalaryData, String> addressColumn;

    private ObservableList<SalaryData> listSalaryStaff = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ViewComboYear();
        ViewComboMonth();
        setupTableColumns();
        getListStaff(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
//        listStaff.setOnMouseClicked(event -> {
//            SalaryData selectedItem = listStaff.getSelectionModel().getSelectedItem();
//            if (selectedItem != null) {
//                // In ra id của dòng được chọn
//                System.out.println("Selected ID: " + selectedItem.getStaffID());
//            }
//        });
    }

    public String formatNumber(int number) {
        if (number < 0) return "0";
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(number);
    }

    private void setupTableColumns() {

        nameStaffColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        dependentsColumn.setCellValueFactory(new PropertyValueFactory<>("dependents"));
        totalDeductionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalDeductions"));
        taxMonthColumn.setCellValueFactory(new PropertyValueFactory<>("tax"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("numberPhone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("gmail"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        sttColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() >= listSalaryStaff.size()) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });

        // Đặt TextFieldTableCell cho cột salaryColumn
        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Xử lý khi người dùng chỉnh sửa giá trị
        salaryColumn.setCellFactory(column -> {
            return new TextFieldTableCell<>(new IntegerStringConverter()) {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatNumber(item)); // Hiển thị số đã được định dạng
                    }
                }
            };
        });
        salaryColumn.setOnEditCommit(event -> {
            System.out.println("Sự kiện changeSalary được kích hoạt");
            changeSalary(event);
        });


        dependentsColumn.setCellFactory(column -> new TableCell<>() {
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
                    setText(formatNumber(item));
                }
            }
        });

        taxMonthColumn.setCellFactory(column -> new TableCell<>() {
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

        // Gán danh sách vào TableView
        listStaff.setItems(listSalaryStaff);

        // Đặt TableView ở chế độ có thể chỉnh sửa
        listStaff.setEditable(true);
    }

    public void ViewComboYear() {
        int currentYear = LocalDate.now().getYear();
        ObservableList<Integer> years = FXCollections.observableArrayList();

        for (int i = 2022; i <= currentYear; i++) {
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
        chooseYear.setOnAction(event -> getListStaff(getYearSelected(), getMonthSelected()));

    }

    public void ViewComboMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        ObservableList<Integer> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        chooseMonth.setItems(months);
        chooseMonth.setValue(currentMonth);

        chooseMonth.setButtonCell(new ListCell<>() {
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
        chooseMonth.setCellFactory(listView -> new ListCell<>() {
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
        chooseMonth.setOnAction(event -> getListStaff(getYearSelected(), getMonthSelected()));
    }

    public int getYearSelected() {
        return chooseYear.getValue();
    }

    public int getMonthSelected() {
        return chooseMonth.getValue();
    }

    public void getListStaff(int year, int month) {
        listSalaryStaff.clear();
        String queryE = "SELECT e.fullname, e.dependents, e.hometown, e.phone, e.email, " +
                "p.salary, p.tax, e.employee_id " +
                "FROM employees e " +
                "LEFT JOIN payroll p ON e.employee_id = p.employee_id " +
                "AND YEAR(p.time_pay) = " + year + " AND MONTH(p.time_pay) = " + month + " " +
                "WHERE e.department_id = '" + navBarController.phongBan.getId() + "'";
        String queryM = "SELECT m.fullname, m.dependents, m.hometown, m.phone, m.email, " +
                "p.salary, p.tax, m.manager_id " +
                "FROM managers m " +
                "LEFT JOIN payroll p ON m.manager_id = p.manager_id " +
                "AND YEAR(p.time_pay) = " + year + " AND MONTH(p.time_pay) = " + month + " " +
                "JOIN departments d ON d.manager_id = m.manager_id " +
                "WHERE d.department_id = '" + navBarController.phongBan.getId() + "'";

        try {
            ResultSet rsE = QueryController.getInstance().Query(queryE);
            while (rsE.next()) {
                TaxCalculator taxCalculator1 = new TaxCalculator();
                SalaryData salaryData1 = new SalaryData();
                salaryData1.setStaffID(rsE.getString("e.employee_id"));
                salaryData1.setName(rsE.getString("fullname"));
                salaryData1.setDependents(rsE.getInt("dependents"));
                salaryData1.setTax(rsE.getObject("tax") != null ? (int) taxCalculator1.taxMonthly(rsE.getInt("p.salary"),rsE.getInt("e.dependents"),getYearSelected()) : 0); // Nếu null thì mặc định là 0
                salaryData1.setSalary(rsE.getObject("salary") != null ? rsE.getInt("salary") : 0); // Nếu null thì mặc định là 0
                salaryData1.setTotalDeductions(calculateDeductions(getYearSelected(), salaryData1.getDependents()));
                salaryData1.setNumberPhone(rsE.getString("phone"));
                salaryData1.setGmail(rsE.getString("email"));
                salaryData1.setAddress(rsE.getString("hometown"));
                listSalaryStaff.add(salaryData1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ResultSet rsM = QueryController.getInstance().Query(queryM);
            while (rsM.next()) {
                TaxCalculator taxCalculator2= new TaxCalculator();
                SalaryData salaryData2 = new SalaryData();
                salaryData2.setStaffID(rsM.getString("m.manager_id"));
                salaryData2.setName(rsM.getString("m.fullname"));
                salaryData2.setDependents(rsM.getInt("m.dependents"));
                salaryData2.setTax(rsM.getObject("tax") != null ? (int) taxCalculator2.taxMonthly(rsM.getInt("p.salary"),rsM.getInt("m.dependents"),getYearSelected()) : 0); // Nếu null thì mặc định là 0
                salaryData2.setSalary(rsM.getObject("salary") != null ? rsM.getInt("salary") : 0);
                salaryData2.setTotalDeductions(calculateDeductions(getYearSelected(), salaryData2.getDependents()));
                salaryData2.setNumberPhone(rsM.getString("m.phone"));
                salaryData2.setGmail(rsM.getString("m.email"));
                salaryData2.setAddress(rsM.getString("m.hometown"));
                listSalaryStaff.add(salaryData2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeSalary(TableColumn.CellEditEvent<SalaryData, Integer> event) {

        // Lấy dòng hiện tại (row) từ sự kiện
        SalaryData selectedStaff = event.getRowValue();
        if (selectedStaff == null) {
            System.out.println("Không có dữ liệu nhân viên được chọn.");
            return;
        }

        int newSalary = event.getNewValue();
        String staffID = selectedStaff.getStaffID();
        if (staffID == null || staffID.isEmpty()) {
            System.out.println("StaffID không hợp lệ.");
            return;
        }
        System.out.println(staffID);

        // Giá trị ngày hiện tại
        String timePay = setCurrentSalaryTimePay();

        // Kết nối cơ sở dữ liệu
        ConnectDB connectDB = new ConnectDB();
        try (Connection conn = connectDB.connect()) {
            // Kiểm tra xem staffID có tồn tại trong bảng payroll hay không
            String checkSql = "SELECT * FROM payroll WHERE (employee_id = ? OR manager_id = ?) AND YEAR(time_pay) = ? AND MONTH(time_pay) = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, staffID);
                checkStmt.setString(2, staffID);
                checkStmt.setInt(3, getYearSelected());
                checkStmt.setInt(4, getMonthSelected());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("có vô update ko");
                    // Nếu tồn tại, thực hiện UPDATE
                    String updateSql = "UPDATE payroll SET salary = ?, time_pay = ? WHERE (employee_id = ? OR manager_id = ?) AND YEAR(time_pay) = ? AND MONTH(time_pay) = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newSalary);
                        updateStmt.setString(2, timePay); // Gán giá trị ngày
                        updateStmt.setString(3, staffID);
                        updateStmt.setString(4, staffID);
                        updateStmt.setInt(5, getYearSelected());
                        updateStmt.setInt(6, getMonthSelected());

                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Cập nhật lương thành công cho StaffID: " + staffID);
                        } else {
                            System.out.println("Không thể cập nhật lương cho StaffID: " + staffID);
                        }
                    }
                } else {
                    System.out.println("vô chỗ thêm ch");
                    // Nếu không tồn tại, thực hiện INSERT
                    String insertSql = "INSERT INTO payroll(manager_id, employee_id, tax, time_pay, salary, total_deduction) VALUES (?, ?, 0, ?, ?, 0)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        if (staffID.contains("EMP")) {
                            insertStmt.setString(1, null);
                            insertStmt.setString(2, staffID);
                        } else if (staffID.contains("MNG")) {
                            insertStmt.setString(1, staffID);
                            insertStmt.setString(2, null);
                        }
                        insertStmt.setString(3, timePay);
                        insertStmt.setInt(4, newSalary);

                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Thêm mới thành công StaffID: " + staffID);
                        } else {
                            System.out.println("Không thể thêm mới StaffID: " + staffID);
                        }
                    }
                }
            }

            // Cập nhật trong danh sách ObservableList và làm mới TableView
            selectedStaff.setSalary(newSalary);
            listStaff.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi xử lý cơ sở dữ liệu: " + e.getMessage());
        }

    }

    private int calculateDeductions ( int year, int dependents){
        YearFee.getFee(year);
        int selfFee = YearFee.getSelfFee();
        int dependentFee = YearFee.getDependentFee();
        int totalFee = selfFee + dependents * dependentFee;
        return totalFee;
    }

    public String setCurrentSalaryTimePay(){
        System.out.println(getYearSelected());
        System.out.println(getMonthSelected());
        System.out.println(getYearSelected()+"-"+getMonthSelected()+"-"+"01");
        return getYearSelected() + "-" + String.format("%02d", getMonthSelected()) + "-01";
    }

    public void HandleCacuTaxMonth(ActionEvent event){
        updatePayroll();
    }
    public void updatePayroll(){
        for(SalaryData salaryData: listSalaryStaff){
            ConnectDB connectDB = new ConnectDB();
            Connection conn = connectDB.connect();

            String query ="UPDATE payroll SET tax = ?, total_deduction = ? WHERE (employee_id = ? OR manager_id = ?) AND YEAR(time_pay) = ? AND MONTH(time_pay) = ?";
            PreparedStatement ptsm = null;
            try {
                TaxCalculator taxCalculator = new TaxCalculator();
                ptsm = conn.prepareStatement(query);
                ptsm.setInt(1,(int) taxCalculator.taxMonthly(salaryData.getSalary(),salaryData.getDependents(),getYearSelected()));
                ptsm.setInt(2,calculateDeductions(getYearSelected(),salaryData.getDependents()));
                ptsm.setString(3,salaryData.getStaffID());
                ptsm.setString(4,salaryData.getStaffID());
                ptsm.setInt(5,getYearSelected());
                ptsm.setInt(6,getMonthSelected());
                int rowsUpdated = ptsm.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Cập nhật lương tax và total cho StaffID: " + salaryData.getStaffID());
                } else {
                    System.out.println("Không thể cập nhật tax và total cho StaffID: " + salaryData.getStaffID());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        getListStaff(getYearSelected(),getMonthSelected());
    }
}