package com.simple.bank.validator;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.service.OtherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component // 注入Spring容器，便于在Service中调用
public class TransactionValidator {

    @Autowired
    private OtherService otherService;

    public void ValidateDepositTransaction(TransactionRequest transactionRequest) {

        if (!otherService.isAccountExists(transactionRequest.getAccountId())){
            throw new BusinessException("NOT_FOUND", "Account with id " + transactionRequest.getAccountId()+ " not found");
        };

        if (transactionRequest.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Deposit amount must > 0");
        }

        if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim().isEmpty()) {
            throw new BusinessException("INVALID_DESC", "Deposit Description must not be empty");
        }
    }

    public void ValidateWithdrawTransaction(TransactionRequest transactionRequest, AccountDTO accountDTO) {

        if (!otherService.isAccountExists(transactionRequest.getAccountId())) {
            throw new BusinessException("NOT_FOUND", "Account with id " + transactionRequest.getAccountId() + " not found");
        };

        if (transactionRequest.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Withdraw amount must > 0");
        }

        if (transactionRequest.getTransactionAmount().compareTo(accountDTO.getBalance()) > 0) {
            throw new BusinessException("INVALID_AMOUNT", "Insufficient balance");
        }

        if (transactionRequest.getDescription() == null || transactionRequest.getDescription().trim().isEmpty()) {
            throw new BusinessException("INVALID_DESC", "Withdraw Description must not be empty");
        }
    }

}
