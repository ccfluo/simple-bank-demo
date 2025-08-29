package com.simple.bank.validator;

import com.simple.bank.api.request.TransferRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.TransferMapper;
import com.simple.bank.service.biz.AccountInquireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferValidator {

    @Autowired
    private AccountInquireService accountService;

    @Autowired
    private TransferMapper transferMapper;
    public void validateBasicInput(TransferRequest request) throws BusinessException {
        // 1. validate input
        if (request.getFromAccountId() == null) {
            throw new BusinessException("INVALID_FIELD", "From account id must not be empty");
        }
        if (request.getToAccountId() == null) {
            throw new BusinessException("INVALID_FIELD", "To account id must not be empty");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("INVALID_TRANSFER", "Cannot transfer to the same account");
        }
        if (request.getTransferAmount() == null || request.getTransferAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_FIELD", "Transfer amount must be > 0");
        }
        if (request.getTransferTraceId() == null || request.getTransferTraceId().trim().isEmpty()) {
            throw new BusinessException("INVALID_FIELD", "Transaction Trace Id must not be empty");
        }
    }

    public void validate(TransferRequest request) throws BusinessException {
        // 2. verify if account existing or not
        AccountDTO fromAccount = accountService.getAccountById(request.getFromAccountId());
        AccountDTO toAccount = accountService.getAccountById(request.getToAccountId());
        if (fromAccount == null || toAccount == null) {
            throw new BusinessException("ACCOUNT_NOT_FOUND", "账户不存在");
        }

        // 3. verify account balance
        if (request.getTransferAmount().compareTo(fromAccount.getBalance()) > 0) {
            throw new BusinessException("INSUFFICIENT_BALANCE", "Insufficient Balance");
        }

        // 4. verify if duplicate transaction
        if (isDuplicateTransfer(request.getTransferTraceId())) {
            throw new BusinessException("DUPLICATE_TRX", "Transfer " + request.getTransferTraceId() + " already processed");
        }
    }

    private boolean isDuplicateTransfer(String traceId) {
        if (transferMapper.countByTraceId(traceId) > 0) {
            return true;
        }
        return false;
    }
}