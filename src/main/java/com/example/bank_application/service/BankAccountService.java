package com.example.bank_application.service;

import com.example.bank_application.dto.bankaccounts.BankAccountRequest;
import com.example.bank_application.model.BankAccount;
import com.example.bank_application.model.User;
import com.example.bank_application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService){
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
    }

    public BankAccount createAccount(BankAccountRequest request, UUID userId){
        User user = userService.findUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount bankAccount = new BankAccount(
                request.getName(),
                user
        );
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
