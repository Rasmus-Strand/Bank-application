package com.example.bank_application.dto;

import com.example.bank_application.model.UserRole;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String username;
    private UserRole userRole;

    public UserResponse(UUID id, String username, UserRole userRole){
        this.id = id;
        this.username = username;
        this.userRole = userRole;


    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getUserRole() {
        return userRole;
    }

}
