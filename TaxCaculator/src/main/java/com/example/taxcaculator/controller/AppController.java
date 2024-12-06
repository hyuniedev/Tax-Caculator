package com.example.taxcaculator.controller;


import com.example.taxcaculator.model.User;

public class AppController {
    private static AppController instance = new AppController();
    private AppController() {}
    public static AppController getInstance() {
        return instance;
    }
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
