package com.simple.bank.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.simple.bank.api.response.CustomerInquireResponse;
import com.simple.bank.api.response.ListOfCustomerResponse;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.service.biz.CustomerInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerInquireController {
    @Autowired
    private CustomerInquireService service;

    @GetMapping("/{customerId}")
    @SentinelResource("cust_info_hot")    //phoebe sentinel hot parm poc
    public ResponseEntity<CustomerInquireResponse> getById(@PathVariable Long customerId){
        CustomerDTO customerDTO = service.getCustomerById(customerId);
        return ResponseEntity.ok(new CustomerInquireResponse(customerDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<ListOfCustomerResponse> getAll(){
        List<CustomerDTO> customerDTOList = service.getAllCustomers();

        if (customerDTOList == null || customerDTOList.isEmpty()) {
            ListOfCustomerResponse responseList = new ListOfCustomerResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No customer found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }
        return ResponseEntity.ok(new ListOfCustomerResponse(customerDTOList));
    }

}
