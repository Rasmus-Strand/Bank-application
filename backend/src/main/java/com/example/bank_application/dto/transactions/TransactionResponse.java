package com.example.bank_application.dto.transactions;

import com.example.bank_application.model.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private TransactionTypes type;
    private UUID fromAccountId;
    private UUID toAccountId;
    private LocalDateTime dateTime;

    public TransactionResponse(UUID transactionId, BigDecimal amount, TransactionTypes type, UUID fromAccountId, UUID toAccountId, LocalDateTime dateTime){
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.dateTime = dateTime;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionTypes getType() {
        return type;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
