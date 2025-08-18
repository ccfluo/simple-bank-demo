package com.simple.bank.exception;

import lombok.Getter;
import lombok.Setter;

public class CustomerNotFound extends RuntimeException {
    public CustomerNotFound(String message) {
        super(message);
    }
    public CustomerNotFound() {
        super("customer not found");
    }
}