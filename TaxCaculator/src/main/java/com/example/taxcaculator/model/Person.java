package com.example.taxcaculator.model;

public class Person {
    private ERole role;
    private String uid;
    private String name;
    private String gmail;
    private String numberPhone;
    private String idPhongBan;
    private String dob;
    private String address;
    private int soNguoiPhuThuoc;

    public Person() {
    }

    public Person(ERole role, String uid, String name, String gmail, String numberPhone, String idPhongBan, String dob, String address, int soNguoiPhuThuoc) {
        this.role = role;
        this.uid = uid;
        this.name = name;
        this.gmail = gmail;
        this.numberPhone = numberPhone;
        this.idPhongBan = idPhongBan;
        this.dob = dob;
        this.address = address;
        this.soNguoiPhuThuoc = soNguoiPhuThuoc;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getIdPhongBan() {
        return idPhongBan;
    }

    public void setIdPhongBan(String idPhongBan) {
        this.idPhongBan = idPhongBan;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSoNguoiPhuThuoc() {
        return soNguoiPhuThuoc;
    }

    public void setSoNguoiPhuThuoc(int soNguoiPhuThuoc) {
        this.soNguoiPhuThuoc = soNguoiPhuThuoc;
    }
}
