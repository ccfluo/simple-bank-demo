package com.simple.bank.validator;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.biz.AccountInquireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component // 注入Spring容器，便于在Service中调用
public class TransactionValidator {

    @Autowired
    private AccountInquireService accountInquireService;

//    @Autowired
//    private TransactionMapper transactionMapper;

    public void ValidateCreditAccountBalance(TransactionRequest transactionRequest) {

        if (!accountInquireService.isAccountExists(transactionRequest.getAccountId())){
            throw new BusinessException("NOT_FOUND", "Account with id " + transactionRequest.getAccountId()+ " not found");
        }

        if (transactionRequest.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_FIELD", "Deposit amount must be > 0");
        }

        if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Deposit Description must not be empty");
        }

        if (transactionRequest.getTransactionTraceId() == null || transactionRequest.getTransactionTraceId().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Transaction Trace Id must not be empty");
        }

//        String transactionTraceId = transactionRequest.getTransactionTraceId();  #phoebe
//        AccountTransaction accountTransaction = transactionMapper.getTransactionsByTraceId(transactionTraceId);
//        if (accountTransaction != null) {
//            throw new BusinessException("DUPLICATE_TRX", "Transaction " + transactionTraceId + " processed by other transaction");
//        }
    }

    public void ValidateDebitAccountBalance(TransactionRequest transactionRequest, BigDecimal originalBalance) {

//        if (!otherService.isAccountExists(transactionRequest.getAccountId())) {
//            throw new BusinessException("NOT_FOUND", "Account with id " + transactionRequest.getAccountId() + " not found");
//        };

        if (transactionRequest.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_FIELD", "Debit amount must > 0");
        }

        if (transactionRequest.getTransactionAmount().compareTo(originalBalance) > 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Insufficient Balance");
        }

        if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Description must not be empty");
        }

        if (transactionRequest.getTransactionTraceId() == null || transactionRequest.getTransactionTraceId().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Transaction Trace Id must not be empty");
        }

//        String transactionTraceId = transactionRequest.getTransactionTraceId();  /phoebe
//        AccountTransaction accountTransaction = transactionMapper.getTransactionsByTraceId(transactionTraceId);
//        if (accountTransaction != null) {
//            throw new BusinessException("DUPLICATE_TRX", "Transaction " + transactionTraceId + " has been processed by other transaction");
//        }
    }

}
