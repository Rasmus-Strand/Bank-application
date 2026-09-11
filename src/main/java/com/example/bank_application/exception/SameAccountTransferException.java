package com.example.bank_application.exception;

public class SameAccountTransferException extends RuntimeException{
    public SameAccountTransferException(String message) {
        super(message);
    }
}
