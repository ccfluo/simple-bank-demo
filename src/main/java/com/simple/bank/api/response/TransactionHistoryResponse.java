package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonPropertyOrder({"response", "transactions"})
@Setter
@Getter
public class TransactionHistoryResponse {
    private Response response;
    private List<AccountTransactionDTO> transactions;

    public TransactionHistoryResponse(List<AccountTransactionDTO> transactions) {
        this.response = new Response("Transaction history retrieved successfully");
        this.transactions = transactions;
    }
}