package com.example.bank_application.controller;

import com.example.bank_application.dto.bankaccounts.*;

import com.example.bank_application.model.BankAccount;
import com.example.bank_application.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService){
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccountResponse>> getAccounts(){
        List<BankAccount> bankAccounts = bankAccountService.findAllBankAccounts();

        List<BankAccountResponse> responses = bankAccounts.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<BankAccountResponse> getAccountById(@PathVariable UUID id){
        Optional<BankAccount> bankAccount = bankAccountService.findById(id);

        if (bankAccount.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        BankAccount foundAccount = bankAccount.get();


        return ResponseEntity.ok(toResponse(foundAccount));
    }

    @PostMapping("/users/{userId}/accounts")
    public ResponseEntity<BankAccountResponse> addAccount(@PathVariable UUID userId, @Valid @RequestBody BankAccountRequest request){
        BankAccount savedAccount = bankAccountService.createAccount(request, userId);


        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedAccount));
    }

    @PostMapping("/accounts/{id}/deposit")
    public ResponseEntity<BankAccountResponse> deposit(@PathVariable UUID id, @Valid @RequestBody DepositRequest request){
        BankAccount updatedAccount = bankAccountService.deposit(id, request.getAmount());

        return ResponseEntity.ok(toResponse(updatedAccount));
    }

     @PostMapping("/accounts/{id}/withdraw")
     public ResponseEntity<BankAccountResponse> withdraw(@PathVariable UUID id, @Valid @RequestBody WithdrawRequest request){
        BankAccount updateAccount = bankAccountService.withdraw(id, request.getAmount());

        return ResponseEntity.ok(toResponse(updateAccount));
     }

    @PostMapping("/accounts/{fromAccountId}/transfer/{toAccountId}")
    public ResponseEntity<BankAccountResponse> transfer(@PathVariable("fromAccountId") UUID fromAccountId, @PathVariable("toAccountId") UUID toAccountId, @Valid @RequestBody TransferRequest request) {
        BankAccount updateAccounts = bankAccountService.transfer(fromAccountId, toAccountId, request.getAmount());

        return ResponseEntity.ok(toResponse(updateAccounts));
     }

    private BankAccountResponse toResponse(BankAccount bankAccount){
        return new BankAccountResponse(bankAccount.getId(), bankAccount.getName(), bankAccount.getAccountNumber(), bankAccount.getBalance(), bankAccount.isActive());
    }

}
