package com.simple.bank.converter;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.entity.AccountTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter {
    public AccountTransactionDTO transactionToDto(AccountTransaction transaction) {
        AccountTransactionDTO dto = new AccountTransactionDTO();
        BeanUtils.copyProperties(transaction, dto);
        return dto;
    }

    public AccountTransaction dtoToTransaction(AccountTransactionDTO dto) {
        AccountTransaction transaction = new AccountTransaction();
        BeanUtils.copyProperties(dto, transaction);
        return transaction;
    }
}