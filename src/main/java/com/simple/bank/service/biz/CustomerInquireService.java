package com.simple.bank.service.biz;

import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.exception.BusinessException;

import java.util.List;

public interface CustomerInquireService {
    CustomerDTO getCustomerById(Long customerId) throws BusinessException;

    List<CustomerDTO> getAllCustomers() throws BusinessException;

    boolean isCustomerExists(Long customerId);

}
