package com.simple.bank.service.biz;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    // deposit with lock
    AccountTransactionDTO deposit(TransactionRequest transactionRequest) throws BusinessException;

//    // deposit without lock
//    AccountTransactionDTO doDeposit(TransactionRequest transactionRequest) throws BusinessException;

    // credit account balance for internal use(no SMS/email sending)
    AccountTransactionDTO creditAccountBalance(TransactionRequest transactionRequest) throws BusinessException;

    // withdraw with lock
    AccountTransactionDTO withdraw(TransactionRequest transactionRequest) throws BusinessException;

//    // withdraw without lock
//    AccountTransactionDTO doWithdraw(TransactionRequest transactionRequest) throws BusinessException;

    // debit account balance for internal use(no SMS/email sending)
    AccountTransactionDTO debitAccountBalance(TransactionRequest transactionRequest) throws BusinessException;

    List<AccountTransactionDTO> getTransactionHistory(Long accountId) throws BusinessException;

    List<AccountTransactionDTO> getTransactionBetween(LocalDateTime start, LocalDateTime end) throws BusinessException;
}