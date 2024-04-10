package com.example.ridepal.Models;

public class Users {
    private String userId;
    private String userName;
    private String userEmail;

    public Users() {
        // Required empty constructor for Firestore
    }

    public Users(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
