package com.example.taxcaculator.model;

public class PhongBan {
    private String id;
    private String idTruongPhong;
    private String tenPhongBan;

    public PhongBan() {
    }

    public PhongBan(String id,  String tenPhongBan, String idTruongPhong) {
        this.id = id;
        this.idTruongPhong = idTruongPhong;
        this.tenPhongBan = tenPhongBan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTruongPhong() {
        return idTruongPhong;
    }

    public void setIdTruongPhong(String idTruongPhong) {
        this.idTruongPhong = idTruongPhong;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }
}
