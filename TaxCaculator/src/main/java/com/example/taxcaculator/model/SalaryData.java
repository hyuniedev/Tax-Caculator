package com.example.taxcaculator.model;

public class SalaryData {
    private String month;
    private int salary;
    private int dependents;
    private int totalDeductions;
    private int tax;
    private String name;
    private int taxYear;

    private String rsTax;
    private String numberPhone;
    private String gmail;
    private String address;
    private String staffID;

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public  SalaryData(){}


    // Getters và Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getDependents() {
        return dependents;
    }

    public void setDependents(int dependents) {
        this.dependents = dependents;
    }

    public int getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(int totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getName(){return  name;}
    public void setName(String name){ this.name = name;}
    public int getTaxYear(){ return taxYear;}
    public void setTaxYear(int taxYear){ this.taxYear = taxYear;}

    public String getRsTax(){ return rsTax;}
    public void setRsTax(String rsTax){ this.rsTax = rsTax;}
}

