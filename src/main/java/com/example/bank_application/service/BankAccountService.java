package com.example.bank_application.service;

import com.example.bank_application.model.BankAccount;
import com.example.bank_application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository){
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccount createAccount(BankAccount bankAccount){
        return bankAccountRepository.save(bankAccount);
    }

    public Optional<BankAccount> findById(UUID id){
        return bankAccountRepository.findById(id);
    }
    public Optional<BankAccount> findByName(String name){
        return bankAccountRepository.findByName(name);
    }
    public List<BankAccount> findAllBankAccounts(){
        return bankAccountRepository.findAll();
    }
}
