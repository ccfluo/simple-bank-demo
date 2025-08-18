package com.simple.bank.exception;

public class AccountNotFound extends RuntimeException {
    public AccountNotFound(String message) {
        super(message);
    }
    public AccountNotFound() {
        super("account not found");
    }

}