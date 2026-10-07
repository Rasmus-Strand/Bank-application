package com.example.bank_application.repository;

import com.example.bank_application.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> findByName(String name);

    List<BankAccount> findByUserId(UUID userId);

}
