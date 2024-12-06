package com.example.taxcaculator.model;

import com.example.taxcaculator.controller.QueryController;

public class User {

    private String userId;
    private String role;


    private User(){
        this.userId = QueryController.getInstance().getNextID("taxdb.users","user_role");
    }

    public User(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public User(String role) {
        this.userId = QueryController.getInstance().getNextID("taxdb.users","user_id");
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
