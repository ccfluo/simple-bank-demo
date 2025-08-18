package com.simple.bank.dto;

import lombok.*;

@Data
public class Response {
    private String code;
    private String message;

    public Response() {
        this.code = "OK";
        this.message = "Process is successfully completed";
    }

    public Response(String message) {
        this.code = "OK";
        this.message = message;
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
