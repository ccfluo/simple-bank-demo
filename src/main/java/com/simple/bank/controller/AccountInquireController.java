package com.simple.bank.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.simple.bank.api.response.AccountInquireResponse;
import com.simple.bank.api.response.ListOfAccountResponse;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.service.AccountInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName AccountController
 * @Description:
 * @Author
 * @Date 2025/8/10
 * @Version V1.0
 **/
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountInquireController {
    @Autowired
    private AccountInquireService service;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountInquireResponse> getById(@PathVariable Long accountId){
        AccountDTO accountDTO = service.getAccountById(accountId);
        return ResponseEntity.ok(new AccountInquireResponse(accountDTO));
    }


    //http://localhost:8085/account/bycustomer?id=123
    @GetMapping("/bycustomer")
    public ResponseEntity<ListOfAccountResponse> getAccountByCustomerId(@RequestParam(name = "id" ,defaultValue = "0") Long id){
        List<AccountDTO> accountDTOList = service.getAccountByCustomerId(id);
        try {                                      //Phoebe sentinel test
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }                                          //Phoebe sentinel test
        return ResponseEntity.ok(new ListOfAccountResponse(accountDTOList));
    }

//    public ResponseEntity<List<CustomerDTO>> getAll() throws CustomerNotFound{
    @GetMapping("/all")
    public ResponseEntity<ListOfAccountResponse> getAll(){
        List<AccountDTO> accountDTOList = service.getAllAccounts();
        return ResponseEntity.ok(new ListOfAccountResponse(accountDTOList));
    }

}
