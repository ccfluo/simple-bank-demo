package com.simple.bank.service;

public interface OtherService {
    boolean isCustomerExists(Long customerId);
    boolean isAccountExists(Long accountId);
}
