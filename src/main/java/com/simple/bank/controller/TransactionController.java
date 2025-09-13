package com.simple.bank.controller;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.api.request.TransferRequest;
import com.simple.bank.api.response.*;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.dto.TransferDTO;
import com.simple.bank.service.biz.TransactionService;
import com.simple.bank.service.biz.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
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

    // /transaction/history/account?id=123
    @GetMapping("/history/account")
    public ResponseEntity<ListOfTransactionResponse> getTransactionHistoryByAccount(
        @RequestParam(name = "id" ,defaultValue = "0") Long id){
        List<AccountTransactionDTO> transactionDTOList = transactionService.getTransactionHistoryByAccountId(id);

        if (transactionDTOList == null || transactionDTOList.isEmpty()) {
            ListOfTransactionResponse responseList = new ListOfTransactionResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No transaction found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }

        return ResponseEntity.ok(new ListOfTransactionResponse(transactionDTOList));
    }

    // /transaction/history/customer?id=123
    @GetMapping("/history/customer")
    public ResponseEntity<ListOfTransactionResponse> getTransactionHistoryByCustomer(
            @RequestParam(name = "id" ,defaultValue = "0") Long id){
        List<AccountTransactionDTO> transactionDTOList = transactionService.getTransactionHistoryByCustomerId(id);

        if (transactionDTOList == null || transactionDTOList.isEmpty()) {
            ListOfTransactionResponse responseList = new ListOfTransactionResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No transaction found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }

        return ResponseEntity.ok(new ListOfTransactionResponse(transactionDTOList));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request) {
        TransferDTO transferDTO = transferService.transfer(request);
        return ResponseEntity.ok(new TransferResponse(transferDTO, "Transfer successfully"));
    }
}