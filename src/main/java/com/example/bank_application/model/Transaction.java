package com.example.bank_application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @NotNull
    private BigDecimal amount;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionTypes type;
    @ManyToOne
    private BankAccount from;
    @ManyToOne
    private BankAccount to;
    @NotNull
    private LocalDateTime dateTime;
    @Id
    @GeneratedValue
    private UUID transactionId;

    protected Transaction(){

    }
    public Transaction(BigDecimal amount, TransactionTypes type, BankAccount from, BankAccount to, LocalDateTime dateTime) {
        this.amount = amount;
        this.type = type;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public BankAccount getFrom() {
        return from;
    }

    public BankAccount getTo() {
        return to;
    }

    public @NotNull BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TransactionTypes getType() {
        return type;
    }

}
