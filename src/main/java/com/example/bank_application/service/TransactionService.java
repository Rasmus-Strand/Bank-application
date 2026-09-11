package com.example.bank_application.service;

import com.example.bank_application.model.Transaction;
import com.example.bank_application.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAllTransactions(){
        return transactionRepository.findAll();
    }
    public Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }

}
