package com.simple.bank.service.impl;

import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.mapper.CustomerMapper;
import com.simple.bank.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtherServiceImpl implements OtherService {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private AccountMapper accountMapper;

    public boolean isCustomerExists(Long customerId) {
        int exists = customerMapper.existsById(customerId);
        return (exists == 1);
    }

    @Override
    public boolean isAccountExists(Long accountId) {
        int exists = accountMapper.existsById(accountId);
        return (exists == 1);
    }

    ;
}
