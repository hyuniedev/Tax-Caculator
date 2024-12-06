package com.example.taxcaculator.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryController {
    private static QueryController queryController = new QueryController();
    private QueryController() {}
    public static QueryController getInstance() {
        return queryController;
    }
    private ConnectDB connectDB = new ConnectDB();
    private Connection connection = connectDB.connect();
    private Statement statement = null;
    public ResultSet Query(String sql) {
        try {
            statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void InsertValue(String sql) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String getNextID(String nameTable, String nameColumn) {
        ResultSet rs = QueryController.getInstance().Query("SELECT "+nameColumn+"\n" +
                "FROM "+nameTable+"\n" +
                "ORDER BY CAST(SUBSTRING("+nameColumn+", 4) AS UNSIGNED) DESC\n" +
                "LIMIT 1;\n");
        try {
            if(rs.next()){
                String s = rs.getString(nameColumn);
                System.out.println("HyuNie said: "+s);
                return s.substring(0,3) + (Integer.parseInt(s.substring(3))+1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
