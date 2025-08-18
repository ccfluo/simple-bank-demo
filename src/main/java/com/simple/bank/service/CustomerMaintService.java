package com.simple.bank.service;

import com.simple.bank.api.request.CustomerAddRequest;
import com.simple.bank.api.request.CustomerUpdateRequest;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.exception.CustomerNotFound;

public interface CustomerMaintService {
    Long createCustomer(CustomerAddRequest customerAddRequest) throws BusinessException;
    CustomerDTO updateCustomer(CustomerUpdateRequest customerUpdateRequest) throws CustomerNotFound, BusinessException;
    int deleteCustomer(Long customerId) throws CustomerNotFound, BusinessException;
}
