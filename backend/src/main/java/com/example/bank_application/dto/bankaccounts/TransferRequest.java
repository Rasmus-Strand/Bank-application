package com.example.bank_application.dto.bankaccounts;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    public TransferRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
