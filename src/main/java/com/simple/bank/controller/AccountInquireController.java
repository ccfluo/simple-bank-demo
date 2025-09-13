package com.simple.bank.controller;

import com.simple.bank.api.response.AccountInquireResponse;
import com.simple.bank.api.response.ListOfAccountResponse;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.service.biz.AccountInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountInquireController {
    @Autowired
    private AccountInquireService service;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountInquireResponse> getById(@PathVariable Long accountId){
        AccountDTO accountDTO = service.getAccountById(accountId);
        return ResponseEntity.ok(new AccountInquireResponse(accountDTO));
    }

    // /account/bycustomer?id=123
    @GetMapping("/bycustomer")
    public ResponseEntity<ListOfAccountResponse> getAccountByCustomerId(@RequestParam(name = "id" ,defaultValue = "0") Long id){
        List<AccountDTO> accountDTOList = service.getAccountByCustomerId(id);
//        try {                                      //Phoebe sentinel test
//            Thread.sleep(200L);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }        //Phoebe sentinel test

        if (accountDTOList == null || accountDTOList.isEmpty()) {
            ListOfAccountResponse responseList = new ListOfAccountResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No account found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }
        return ResponseEntity.ok(new ListOfAccountResponse(accountDTOList));
    }

//    public ResponseEntity<List<CustomerDTO>> getAll() throws CustomerNotFound{
    @GetMapping("/all") // TODO:pagination to be handled
    public ResponseEntity<ListOfAccountResponse> getAll(){
        List<AccountDTO> accountDTOList = service.getAllAccounts();

        if (accountDTOList == null || accountDTOList.isEmpty()) {
            ListOfAccountResponse responseList = new ListOfAccountResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No account found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }
        return ResponseEntity.ok(new ListOfAccountResponse(accountDTOList));
    }

}
