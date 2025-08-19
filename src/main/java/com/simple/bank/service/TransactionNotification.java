package com.simple.bank.service;

import com.simple.bank.dto.AccountTransactionDTO;

public interface TransactionNotification {
    void sendTransactionNotification(AccountTransactionDTO accountTransactionDTO, String mobile, String email);
}
