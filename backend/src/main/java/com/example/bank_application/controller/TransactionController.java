package com.example.bank_application.controller;


import com.example.bank_application.dto.transactions.TransactionResponse;
import com.example.bank_application.model.Transaction;
import com.example.bank_application.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(){
        List<Transaction> transactions = transactionService.findAllTransactions();

        List<TransactionResponse> responses = transactions.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);

    }

    private TransactionResponse toResponse(Transaction transaction) {

        UUID fromAccountId = transaction.getFrom() != null
                ? transaction.getFrom().getId()
                : null;

        UUID toAccountId = transaction.getTo() != null
                ? transaction.getTo().getId()
                : null;

        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getType(),
                fromAccountId,
                toAccountId,
                transaction.getDateTime()
        );
    }
}
