package com.simple.bank.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private String code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}