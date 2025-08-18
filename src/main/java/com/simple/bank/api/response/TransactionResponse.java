package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "transaction"})
@Setter
@Getter
public class TransactionResponse {
    private Response response;
    private AccountTransactionDTO transaction;

    public TransactionResponse(AccountTransactionDTO transaction, String message) {
        this.response = new Response(message);
        this.transaction = transaction;
    }
}