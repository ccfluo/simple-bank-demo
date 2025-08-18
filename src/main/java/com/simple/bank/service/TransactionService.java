package com.simple.bank.service;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    AccountTransactionDTO deposit(TransactionRequest transactionRequest)
            throws AccountNotFound, BusinessException;

    AccountTransactionDTO withdraw(TransactionRequest transactionRequest)
            throws AccountNotFound, BusinessException;

    List<AccountTransactionDTO> getTransactionHistory(Long accountId) throws AccountNotFound;
}