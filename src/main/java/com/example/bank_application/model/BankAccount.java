package com.example.bank_application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal balance;
    @Column(unique = true, nullable = false)
    private int accountNumber;
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    private User user;
    private boolean active;

    protected BankAccount(){

    }
    public BankAccount(String name, BigDecimal balance, User user) {
        this.name = name;
        this.balance = balance;
        this.user = user;
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
    public void deactivate(){
        active = false;
    }

    public User getUser() {
        return user;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}
