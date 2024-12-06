package com.example.taxcaculator.model;


import com.example.taxcaculator.controller.ConnectDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class YearFee {
    private static int selfFee;
    private static int dependentFee;

    private YearFee() {}

    public static int getSelfFee() {
        return selfFee;
    }

    public static int getDependentFee() {
        return dependentFee;
    }


    public static void getFee(int year){
        ConnectDB connectDB = new ConnectDB();
        Connection conn = connectDB.connect();
        String query = "SELECT * FROM taxdb.deductions WHERE year_apply=" + year ;
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()){
                selfFee = rs.getInt("self_fee");
                dependentFee = rs.getInt("dependents_fee");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
