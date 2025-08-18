package com.simple.bank.controller;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.api.response.TransactionResponse;
import com.simple.bank.api.response.TransactionHistoryResponse;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        AccountTransactionDTO accountTransactionDTO = transactionService.deposit(transactionRequest);
        return ResponseEntity.ok(new TransactionResponse(accountTransactionDTO, "Deposit successful"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest transactionRequest) {
        AccountTransactionDTO accountTransactionDTO = transactionService.withdraw(transactionRequest);
        return ResponseEntity.ok(new TransactionResponse(accountTransactionDTO, "Withdraw successful"));
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(@PathVariable Long accountId) {
        List<AccountTransactionDTO> accountTransactionDTOs = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(new TransactionHistoryResponse(accountTransactionDTOs));
    }
}