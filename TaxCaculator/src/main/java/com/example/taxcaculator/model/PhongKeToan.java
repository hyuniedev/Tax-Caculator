package com.example.taxcaculator.model;

public class PhongKeToan {
    private int id;
    private String numberPhong;

    public PhongKeToan(String numberPhong, int id) {
        this.numberPhong = numberPhong;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumberPhong() {
        return numberPhong;
    }

    public void setNumberPhong(String numberPhong) {
        this.numberPhong = numberPhong;
    }
}
