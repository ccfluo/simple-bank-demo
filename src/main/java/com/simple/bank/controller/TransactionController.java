package com.simple.bank.controller;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.api.request.TransferRequest;
import com.simple.bank.api.response.TransactionResponse;
import com.simple.bank.api.response.ListOfTransactionResponse;
import com.simple.bank.api.response.TransferResponse;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.TransferDTO;
import com.simple.bank.service.biz.TransactionService;
import com.simple.bank.service.biz.TransferService;
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
    @Autowired
    private TransferService transferService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        AccountTransactionDTO accountTransactionDTO = transactionService.deposit(transactionRequest);
        return ResponseEntity.ok(new TransactionResponse(accountTransactionDTO, "Deposit successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest transactionRequest) {
        AccountTransactionDTO accountTransactionDTO = transactionService.withdraw(transactionRequest);
        return ResponseEntity.ok(new TransactionResponse(accountTransactionDTO, "Withdraw successfully"));
    }

    @GetMapping("/history/{accountId}") // TODO:pagination to be handled
    public ResponseEntity<ListOfTransactionResponse> getTransactionHistory(@PathVariable Long accountId) {
        List<AccountTransactionDTO> accountTransactionDTOs = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(new ListOfTransactionResponse(accountTransactionDTOs));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request) {
        TransferDTO transferDTO = transferService.transfer(request);
        return ResponseEntity.ok(new TransferResponse(transferDTO, "Transfer succesfully"));
    }
}