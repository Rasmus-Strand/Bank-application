package com.example.bank_application.dto.users;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public UserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
