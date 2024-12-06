package com.example.taxcaculator.model;


import com.example.taxcaculator.controller.QueryController;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThongBao {
    private String id;
    private String idNhanVien;
    private int newNPT;

    public ThongBao(String id, String idNhanVien, int newNPT) {
        this.id = id;
        this.idNhanVien = idNhanVien;
        this.newNPT = newNPT;
    }

    public void XacNhan(){
        // TODO: Truy xuat nhan vien qua idNhanVien, thay doi so nguoi phu thuoc cua nhan vien
        ResultSet rs = QueryController.getInstance().Query("select employee_id, manager_id from taxdb.notifications where noti_id = '"+id+"';");
        try {
            if(rs.next()){
                if(rs.getString("manager_id") !=null && rs.getString("manager_id").equals(idNhanVien)){
                    QueryController.getInstance().InsertValue("Update taxdb.managers set dependents = " + newNPT + " where manager_id = '"+idNhanVien+"';");
                }else if(rs.getString("employee_id") !=null && rs.getString("employee_id").equals(idNhanVien)){
                    QueryController.getInstance().InsertValue("Update taxdb.employees set dependents = " + newNPT + " where employee_id = '"+idNhanVien+"';");
                }
            }
            XoaThongBao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void TuChoi(){
        XoaThongBao();
    }

    private void XoaThongBao(){
        // TODO: Xoa thong bao nay khoi database
        QueryController.getInstance().InsertValue("delete from taxdb.notifications where noti_id = '"+id+"';");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(String idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public int getNewNPT() {
        return newNPT;
    }

    public void setNewNPT(int newNPT) {
        this.newNPT = newNPT;
    }
}
