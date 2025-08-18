package com.simple.bank.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.simple.bank.api.response.CustomerInquireResponse;
import com.simple.bank.api.response.ListOfCustomerResponse;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.service.CustomerInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CustomerController
 * @Description:
 * @Author
 * @Date 2025/8/10
 * @Version V1.0
 **/
@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerInquireController {
    @Autowired
    private CustomerInquireService service;

    @GetMapping("/{customerId}")
    @SentinelResource("cust_info_hot")    //phoebe sentinel hot parm poc
//    public ResponseEntity<CustomerDTO> getById(@PathVariable Long customerId) throws CustomerNotFound{
    public ResponseEntity<CustomerInquireResponse> getById(@PathVariable Long customerId){
        CustomerDTO customerDTO = service.getCustomerById(customerId);
        return ResponseEntity.ok(new CustomerInquireResponse(customerDTO));
    }

//    public ResponseEntity<List<CustomerDTO>> getAll() throws CustomerNotFound{
    @GetMapping("/all")
    public ResponseEntity<ListOfCustomerResponse> getAll(){
        List<CustomerDTO> customerDTOList = service.getAllCustomers();
        return ResponseEntity.ok(new ListOfCustomerResponse(customerDTOList));
    }

}
