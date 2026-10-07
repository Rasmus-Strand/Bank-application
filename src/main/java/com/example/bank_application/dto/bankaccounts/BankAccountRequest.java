package com.example.bank_application.dto.bankaccounts;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class BankAccountRequest {
    @NotBlank
    private String name;

    public BankAccountRequest(){
    }

    public BankAccountRequest(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
