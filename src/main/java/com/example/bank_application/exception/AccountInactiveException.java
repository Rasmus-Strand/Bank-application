package com.example.bank_application.exception;

public class AccountInactiveException extends RuntimeException{
    public AccountInactiveException(String message) {
        super(message);
    }
}
