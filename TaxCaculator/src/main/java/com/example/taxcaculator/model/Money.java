package com.example.taxcaculator.model;

import java.text.DecimalFormat;

public class Money {
    public static String format(String s){
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(Double.parseDouble(s.trim())).replace(',','.');
    }

    public static int unFormat(String s){
        return Integer.parseInt(s.trim().replace(".",""));
    }
}
