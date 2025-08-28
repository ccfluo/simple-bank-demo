package com.simple.bank.service.biz;

public interface OtherService {
    boolean isCustomerExists(Long customerId);
    boolean isAccountExists(Long accountId);
}
