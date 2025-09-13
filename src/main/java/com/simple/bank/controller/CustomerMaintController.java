package com.simple.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simple.bank.api.request.CustomerAddRequest;
import com.simple.bank.api.request.CustomerUpdateRequest;
import com.simple.bank.api.response.CustomerAddResponse;
import com.simple.bank.api.response.CustomerDeleteResponse;
import com.simple.bank.api.response.CustomerUpdateResponse;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.service.biz.CustomerMaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerMaintController {
    @Autowired
    private CustomerMaintService service;

    @PostMapping("/create")
    public ResponseEntity<CustomerAddResponse> createCustomer(@RequestBody CustomerAddRequest customerAddRequest) throws JsonProcessingException {
        Long newCustomerId = service.createCustomer(customerAddRequest);
        String message = "New customer created, Customer id = " + newCustomerId;
        return ResponseEntity.ok(new CustomerAddResponse(message));
    }

    @PostMapping("/update")
    public ResponseEntity<CustomerUpdateResponse>  updateCustomer(@RequestBody CustomerUpdateRequest customerUpdateRequest) {
        CustomerDTO updatedCustomerDTO= service.updateCustomer(customerUpdateRequest);
        String message = "Customer updated successfully";
        return ResponseEntity.ok(new CustomerUpdateResponse(customerUpdateRequest.getCustomer(), message));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<CustomerDeleteResponse> getById(@PathVariable Long customerId){
        service.deleteCustomer(customerId);
        String message = "Customer deleted, Customer id = " + customerId;
        return ResponseEntity.ok(new CustomerDeleteResponse(message));
    }
}
