package com.example.bank_application.service;

import com.example.bank_application.dto.bankaccounts.BankAccountRequest;
import com.example.bank_application.model.BankAccount;
import com.example.bank_application.model.Transaction;
import com.example.bank_application.model.TransactionTypes;
import com.example.bank_application.model.User;
import com.example.bank_application.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService, TransactionService transactionService){
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.transactionService = transactionService;
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


     @Transactional
    public BankAccount deposit (UUID toAccountId, BigDecimal amount){
        BankAccount bankAccount = bankAccountRepository.findById(toAccountId).orElseThrow(() -> new RuntimeException("Account not found"));

        if (!bankAccount.isActive()){
            throw new RuntimeException("Account is inactive");
        }
        bankAccount.deposit(amount);
        Transaction transaction = new Transaction(amount, TransactionTypes.DEPOSIT,null, bankAccount, LocalDateTime.now());
        transactionService.saveTransaction(transaction);
        return bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public BankAccount withdraw(UUID fromAccountId, BigDecimal amount){
        BankAccount bankAccount = bankAccountRepository.findById(fromAccountId).orElseThrow(() -> new RuntimeException("Account not found"));

        if (!bankAccount.isActive()){
            throw new RuntimeException("Account is inactive");
        }
        bankAccount.withdraw(amount);
        Transaction transaction = new Transaction(amount, TransactionTypes.WITHDRAW,bankAccount, null, LocalDateTime.now());
        transactionService.saveTransaction(transaction);
        return bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public BankAccount transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount){
        BankAccount bankAccountFrom = bankAccountRepository.findById(fromAccountId).orElseThrow(() -> new RuntimeException("Account not found"));
        BankAccount bankAccountTo = bankAccountRepository.findById(toAccountId).orElseThrow(() -> new RuntimeException("Account not found"));

        if (!bankAccountFrom.isActive() || !bankAccountTo.isActive()){
            throw new RuntimeException("Account is inactive");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        bankAccountFrom.withdraw(amount);
        bankAccountTo.deposit(amount);
        Transaction transaction = new Transaction(amount, TransactionTypes.TRANSFER, bankAccountFrom, bankAccountTo, LocalDateTime.now());
        transactionService.saveTransaction(transaction);
        return bankAccountFrom;
    }
}
