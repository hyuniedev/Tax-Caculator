package com.example.taxcaculator.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
static final String DB_URL ="jdbc:mysql://127.0.0.1:3306/taxdb";
    static final String USER ="root";
    static final String PASS="8848";

    public Connection connect(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
