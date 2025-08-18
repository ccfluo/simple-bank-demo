package com.simple.bank.service;

import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.exception.CustomerNotFound;

import java.util.List;

public interface CustomerInquireService {
    CustomerDTO getCustomerById(Long customerId) throws CustomerNotFound;

    List<CustomerDTO> getAllCustomers() throws CustomerNotFound;

}
