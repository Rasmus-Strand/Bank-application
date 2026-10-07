package com.example.bank_application.service;

import com.example.bank_application.model.Transaction;
import com.example.bank_application.model.TransactionTypes;
import com.example.bank_application.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void findAllTransactions_shouldReturnAllTransactions() {

        List<Transaction> transactions = List.of(
                new Transaction(
                        new BigDecimal("100.00"),
                        TransactionTypes.DEPOSIT,
                        null,
                        null,
                        LocalDateTime.now()
                ),
                new Transaction(
                        new BigDecimal("50.00"),
                        TransactionTypes.WITHDRAW,
                        null,
                        null,
                        LocalDateTime.now()
                )
        );

        when(transactionRepository.findAll())
                .thenReturn(transactions);

        List<Transaction> result =
                transactionService.findAllTransactions();

        assertEquals(transactions, result);

        verify(transactionRepository)
                .findAll();
    }

    @Test
    void saveTransaction_shouldSaveAndReturnTransaction() {

        Transaction transaction =
                new Transaction(
                        new BigDecimal("100.00"),
                        TransactionTypes.DEPOSIT,
                        null,
                        null,
                        LocalDateTime.now()
                );

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Transaction result =
                transactionService.saveTransaction(transaction);

        assertEquals(transaction, result);

        verify(transactionRepository)
                .save(transaction);
    }


}