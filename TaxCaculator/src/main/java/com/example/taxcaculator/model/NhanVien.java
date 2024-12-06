package com.example.taxcaculator.model;

public class NhanVien extends Person{
    public NhanVien() {
        super.setRole(ERole.NhanVien);
    }

    public NhanVien(String uid, String name, String gmail, String numberPhone, String idPhongBan, String dob, String address, int soNguoiPhuThuoc) {
        super(ERole.NhanVien, uid, name, gmail, numberPhone, idPhongBan, dob, address, soNguoiPhuThuoc);
    }
}
