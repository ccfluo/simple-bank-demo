package com.simple.bank.service;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    AccountTransactionDTO deposit(TransactionRequest transactionRequest) throws BusinessException;

    AccountTransactionDTO withdraw(TransactionRequest transactionRequest) throws BusinessException;

    List<AccountTransactionDTO> getTransactionHistory(Long accountId) throws BusinessException;

    List<AccountTransactionDTO> getTransactionBetween(LocalDateTime start, LocalDateTime end) throws BusinessException;
}