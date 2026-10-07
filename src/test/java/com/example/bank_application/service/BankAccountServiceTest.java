package com.example.bank_application.service;

import com.example.bank_application.dto.bankaccounts.BankAccountRequest;
import com.example.bank_application.exception.*;
import com.example.bank_application.model.*;
import com.example.bank_application.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BankAccountService bankAccountService;


    @Test
    void createAccount_shouldCreateAndSaveAccount() {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccountRequest request =
                new BankAccountRequest("Savings");

        when(userService.findUserById(userId))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        BankAccount result =
                bankAccountService.createAccount(request, userId);

        assertEquals("Savings", result.getName());
        assertEquals(user, result.getUser());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertTrue(result.isActive());

        verify(userService).findUserById(userId);
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createAccount_shouldThrowExceptionWhenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        BankAccountRequest request =
                new BankAccountRequest("Savings");

        when(userService.findUserById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.createAccount(request, userId)
        );

        verify(userService).findUserById(userId);
        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void deposit_shouldIncreaseBalanceAndSaveTransaction() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(transactionService.saveTransaction(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        BankAccount result =
                bankAccountService.deposit(accountId, amount);

        assertEquals(
                new BigDecimal("100.00"),
                result.getBalance()
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService)
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository)
                .save(account);
    }

    @Test
    void deposit_shouldThrowExceptionWhenAccountDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> bankAccountService.deposit(accountId, amount)
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }
    @Test
    void deposit_shouldThrowExceptionWhenAccountIsInactive() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deactivate();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountInactiveException.class,
                () -> bankAccountService.deposit(accountId, amount)
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void withdraw_shouldDecreaseBalanceAndSaveTransaction() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(transactionService.saveTransaction(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        BankAccount result =
                bankAccountService.withdraw(accountId, amount);

        assertEquals(
                new BigDecimal("100.00"),
                result.getBalance()
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService)
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository)
                .save(account);
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> bankAccountService.withdraw(accountId, amount)
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountIsInactive() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deactivate();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountInactiveException.class,
                () -> bankAccountService.withdraw(accountId, amount)
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenBalanceIsInsufficient() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deposit(new BigDecimal("50.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientFundsException.class,
                () -> bankAccountService.withdraw(accountId, amount)
        );

        assertEquals(
                new BigDecimal("50.00"),
                account.getBalance()
        );

        verify(bankAccountRepository)
                .findById(accountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));

        verify(bankAccountRepository, never())
                .save(any(BankAccount.class));
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccountsAndSaveTransaction() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        BankAccount toAccount =
                new BankAccount("Checking", user);

        fromAccount.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));

        when(transactionService.saveTransaction(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        BankAccount result =
                bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                );

        assertEquals(
                new BigDecimal("100.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("100.00"),
                toAccount.getBalance()
        );

        assertEquals(fromAccount, result);

        verify(bankAccountRepository)
                .findById(fromAccountId);

        verify(bankAccountRepository)
                .findById(toAccountId);

        verify(transactionService)
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenFromAccountDoesNotExist() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                )
        );

        verify(bankAccountRepository)
                .findById(fromAccountId);

        verify(bankAccountRepository, never())
                .findById(toAccountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenToAccountDoesNotExist() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        fromAccount.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                )
        );

        assertEquals(
                new BigDecimal("200.00"),
                fromAccount.getBalance()
        );

        verify(bankAccountRepository)
                .findById(fromAccountId);

        verify(bankAccountRepository)
                .findById(toAccountId);

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenFromAccountIsInactive() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        BankAccount toAccount =
                new BankAccount("Checking", user);

        fromAccount.deposit(new BigDecimal("200.00"));
        fromAccount.deactivate();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                AccountInactiveException.class,
                () -> bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                )
        );

        assertEquals(
                new BigDecimal("200.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                BigDecimal.ZERO,
                toAccount.getBalance()
        );

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenToAccountIsInactive() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        BankAccount toAccount =
                new BankAccount("Checking", user);

        fromAccount.deposit(new BigDecimal("200.00"));
        toAccount.deactivate();

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                AccountInactiveException.class,
                () -> bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                )
        );

        assertEquals(
                new BigDecimal("200.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                BigDecimal.ZERO,
                toAccount.getBalance()
        );

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }
    @Test
    void transfer_shouldThrowExceptionWhenTransferringToSameAccount() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                SameAccountTransferException.class,
                () -> bankAccountService.transfer(
                        accountId,
                        accountId,
                        amount
                )
        );

        assertEquals(
                new BigDecimal("200.00"),
                account.getBalance()
        );

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenBalanceIsInsufficient() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        BankAccount toAccount =
                new BankAccount("Checking", user);

        fromAccount.deposit(new BigDecimal("50.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                InsufficientFundsException.class,
                () -> bankAccountService.transfer(
                        fromAccountId,
                        toAccountId,
                        amount
                )
        );

        assertEquals(
                new BigDecimal("50.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                BigDecimal.ZERO,
                toAccount.getBalance()
        );

        verify(transactionService, never())
                .saveTransaction(any(Transaction.class));
    }

    @Test
    void deposit_shouldCreateCorrectTransaction() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        bankAccountService.deposit(accountId, amount);

        verify(transactionService)
                .saveTransaction(transactionCaptor.capture());

        Transaction transaction =
                transactionCaptor.getValue();

        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionTypes.DEPOSIT, transaction.getType());
        assertEquals(account, transaction.getTo());
        assertEquals(null, transaction.getFrom());
    }

    @Test
    void withdraw_shouldCreateCorrectTransaction() {

        UUID accountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount account =
                new BankAccount("Savings", user);

        account.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        bankAccountService.withdraw(accountId, amount);

        verify(transactionService)
                .saveTransaction(transactionCaptor.capture());

        Transaction transaction =
                transactionCaptor.getValue();

        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionTypes.WITHDRAW, transaction.getType());
        assertEquals(account, transaction.getFrom());
        assertEquals(null, transaction.getTo());
    }

    @Test
    void transfer_shouldCreateCorrectTransaction() {

        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        BankAccount fromAccount =
                new BankAccount("Savings", user);

        BankAccount toAccount =
                new BankAccount("Checking", user);

        fromAccount.deposit(new BigDecimal("200.00"));

        BigDecimal amount =
                new BigDecimal("100.00");

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));

        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        bankAccountService.transfer(
                fromAccountId,
                toAccountId,
                amount
        );

        verify(transactionService)
                .saveTransaction(transactionCaptor.capture());

        Transaction transaction =
                transactionCaptor.getValue();

        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionTypes.TRANSFER, transaction.getType());
        assertEquals(fromAccount, transaction.getFrom());
        assertEquals(toAccount, transaction.getTo());
    }
    @Test
    void userOwnsAccount_shouldReturnTrueWhenUserOwnsAccount() {

        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        BankAccount account =
                new BankAccount("Savings", user);

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        boolean result =
                bankAccountService.userOwnsAccount(
                        accountId,
                        "testuser"
                );

        assertTrue(result);

        verify(userService)
                .findByUsername("testuser");

        verify(bankAccountRepository)
                .findById(accountId);
    }

    @Test
    void userOwnsAccount_shouldReturnFalseWhenUserDoesNotOwnAccount() {

        UUID accountId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User loggedInUser = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        User accountOwner = new User(
                "otheruser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                loggedInUser,
                "id",
                userId
        );

        ReflectionTestUtils.setField(
                accountOwner,
                "id",
                otherUserId
        );

        BankAccount account =
                new BankAccount("Savings", accountOwner);

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(loggedInUser));

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        boolean result =
                bankAccountService.userOwnsAccount(
                        accountId,
                        "testuser"
                );

        assertFalse(result);

        verify(userService)
                .findByUsername("testuser");

        verify(bankAccountRepository)
                .findById(accountId);
    }

    @Test
    void userOwnsAccount_shouldThrowExceptionWhenUserDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        when(userService.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.userOwnsAccount(
                        accountId,
                        "unknown"
                )
        );

        verify(userService)
                .findByUsername("unknown");

        verify(bankAccountRepository, never())
                .findById(accountId);
    }

    @Test
    void userOwnsAccount_shouldThrowExceptionWhenAccountDoesNotExist() {

        UUID accountId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> bankAccountService.userOwnsAccount(
                        accountId,
                        "testuser"
                )
        );

        verify(userService)
                .findByUsername("testuser");

        verify(bankAccountRepository)
                .findById(accountId);
    }

    @Test
    void findBankAccountsForUser_shouldReturnUsersAccounts() {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        BankAccount account1 =
                new BankAccount("Savings", user);

        BankAccount account2 =
                new BankAccount("Checking", user);

        List<BankAccount> accounts =
                List.of(account1, account2);

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(bankAccountRepository.findByUserId(userId))
                .thenReturn(accounts);

        List<BankAccount> result =
                bankAccountService.findBankAccountsForUser("testuser");

        assertEquals(accounts, result);

        verify(userService)
                .findByUsername("testuser");

        verify(bankAccountRepository)
                .findByUserId(userId);
    }

    @Test
    void findBankAccountsForUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userService.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.findBankAccountsForUser("unknown")
        );

        verify(userService)
                .findByUsername("unknown");

        verify(bankAccountRepository, never())
                .findByUserId(any());
    }

    @Test
    void userMatchesId_shouldReturnTrueWhenIdsMatch() {

        UUID userId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        boolean result =
                bankAccountService.userMatchesId(
                        "testuser",
                        userId
                );

        assertTrue(result);

        verify(userService)
                .findByUsername("testuser");
    }

    @Test
    void userMatchesId_shouldReturnFalseWhenIdsDoNotMatch() {

        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User user = new User(
                "testuser",
                "hashedPassword",
                UserRole.USER
        );

        ReflectionTestUtils.setField(
                user,
                "id",
                userId
        );

        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        boolean result =
                bankAccountService.userMatchesId(
                        "testuser",
                        otherUserId
                );

        assertFalse(result);

        verify(userService)
                .findByUsername("testuser");
    }

    @Test
    void userMatchesId_shouldThrowExceptionWhenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        when(userService.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bankAccountService.userMatchesId(
                        "unknown",
                        userId
                )
        );

        verify(userService)
                .findByUsername("unknown");
    }


}