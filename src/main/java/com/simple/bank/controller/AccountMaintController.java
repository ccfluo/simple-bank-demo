package com.simple.bank.controller;

import com.simple.bank.api.request.AccountAddRequest;
import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.api.response.AccountAddResponse;
import com.simple.bank.api.response.AccountDeleteResponse;
import com.simple.bank.api.response.AccountUpdateResponse;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.service.AccountMaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName AccountMaintController
 * @Description:
 * @Author
 * @Date 2025/8/10
 * @Version V1.0
 **/
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountMaintController {
    @Autowired
    private AccountMaintService service;


    @PostMapping("/create")
    public ResponseEntity<AccountAddResponse> createAccount(@RequestBody AccountAddRequest accountAddRequest) {
        Long newCustomerId = service.createAccount(accountAddRequest);
        String message = "New account created, Account id = " + newCustomerId;
        return ResponseEntity.ok(new AccountAddResponse(message));
    }

    @PostMapping("/update")
    public ResponseEntity<AccountUpdateResponse>  updateAccount(@RequestBody AccountUpdateRequest accountUpdateRequest) {
        AccountDTO updatedAccountDTO= service.updateAccount(accountUpdateRequest);
        String message = "Customer updated successfully";
        return ResponseEntity.ok(new AccountUpdateResponse(updatedAccountDTO, message));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<AccountDeleteResponse> getById(@PathVariable Long accountId){
        service.deleteAccount(accountId);
        String message = "Account deleted, Account id = " + accountId;
        return ResponseEntity.ok(new AccountDeleteResponse(message));
    }
}
