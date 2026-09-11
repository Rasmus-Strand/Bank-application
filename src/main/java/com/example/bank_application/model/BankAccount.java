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
    @Column(
            name = "account_number",
            unique = true,
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Long accountNumber;
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    private User user;
    private boolean active;

    protected BankAccount(){

    }
    public BankAccount(String name, User user) {
        this.name = name;
        this.balance = BigDecimal.ZERO;
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

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void deposit(BigDecimal amount){
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }
}
