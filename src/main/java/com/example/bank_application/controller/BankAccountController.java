package com.example.bank_application.controller;

import com.example.bank_application.dto.bankaccounts.*;

import com.example.bank_application.model.BankAccount;
import com.example.bank_application.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<BankAccountResponse>> getAccounts(
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        List<BankAccount> bankAccounts;

        if (isAdmin) {
            bankAccounts = bankAccountService.findAllBankAccounts();
        } else {
            bankAccounts = bankAccountService.findBankAccountsForUser(
                    authentication.getName());
        }

        List<BankAccountResponse> responses = bankAccounts.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<BankAccountResponse> getAccountById(
            @PathVariable UUID id,
            Authentication authentication) {

        String username = authentication.getName();

        Optional<BankAccount> bankAccount =
                bankAccountService.findById(id);

        if (bankAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bankAccountService.userOwnsAccount(id, authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(toResponse(bankAccount.get()));
    }

    @PostMapping("/users/{userId}/accounts")
    public ResponseEntity<BankAccountResponse> addAccount(
            @PathVariable UUID userId,
            @Valid @RequestBody BankAccountRequest request,
            Authentication authentication) {

        if (!isUser(authentication)) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        if (!bankAccountService.userMatchesId(username, userId)) {
            return ResponseEntity.status(403).build();
        }

        BankAccount savedAccount =
                bankAccountService.createAccount(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedAccount));
    }

    @PostMapping("/accounts/{id}/deposit")
    public ResponseEntity<BankAccountResponse> deposit(
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        if (!isUser(authentication)) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        if (!bankAccountService.userOwnsAccount(id, username)) {
            return ResponseEntity.status(403).build();
        }

        BankAccount updatedAccount =
                bankAccountService.deposit(id, request.getAmount());

        return ResponseEntity.ok(toResponse(updatedAccount));
    }

    @PostMapping("/accounts/{id}/withdraw")
    public ResponseEntity<BankAccountResponse> withdraw(
            @PathVariable UUID id,
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        if (!isUser(authentication)) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        if (!bankAccountService.userOwnsAccount(id, username)) {
            return ResponseEntity.status(403).build();
        }

        BankAccount updatedAccount =
                bankAccountService.withdraw(id, request.getAmount());

        return ResponseEntity.ok(toResponse(updatedAccount));
    }

    @PostMapping("/accounts/{fromAccountId}/transfer/{toAccountId}")
    public ResponseEntity<BankAccountResponse> transfer(
            @PathVariable UUID fromAccountId,
            @PathVariable UUID toAccountId,
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        if (!isUser(authentication)) {
            return ResponseEntity.status(403).build();
        }

        String username = authentication.getName();

        if (!bankAccountService.userOwnsAccount(fromAccountId, username)) {
            return ResponseEntity.status(403).build();
        }

        BankAccount updatedAccount =
                bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        request.getAmount()
                );

        return ResponseEntity.ok(toResponse(updatedAccount));
    }

    private boolean isUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_USER"));
    }

    private BankAccountResponse toResponse(BankAccount bankAccount){
        return new BankAccountResponse(bankAccount.getId(), bankAccount.getName(), bankAccount.getAccountNumber(), bankAccount.getBalance(), bankAccount.isActive());
    }

}
