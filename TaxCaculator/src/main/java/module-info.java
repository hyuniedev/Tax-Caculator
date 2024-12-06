module com.example.taxcaculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.example.taxcaculator.model to javafx.base;
    opens com.example.taxcaculator.controller to javafx.fxml;
    opens com.example.taxcaculator to javafx.fxml;
    exports com.example.taxcaculator;
}