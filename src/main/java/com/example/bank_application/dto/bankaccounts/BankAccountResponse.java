package com.example.bank_application.dto.bankaccounts;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountResponse {
    private UUID id;
    private String name;
    private Long accountNumber;
    private BigDecimal balance;
    private boolean active;

    public BankAccountResponse(UUID id, String name, Long accountNumber, BigDecimal balance, boolean active){
        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}
