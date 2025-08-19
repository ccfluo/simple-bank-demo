package com.simple.bank.service.impl;

import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.converter.TransactionConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.AccountTransaction;
import com.simple.bank.exception.AccountNotFound;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.TransactionMapper;
import com.simple.bank.service.*;
import com.simple.bank.validator.TransactionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountInquireService accountInquireService;

    @Autowired
    private AccountMaintService accountMaintService;

    @Autowired
    private TransactionConverter transactionConverter;

    @Autowired
    private TransactionValidator transactionValidator;

    @Autowired
    private TransactionNotification transactionNotification;

    @Autowired
    private CustomerInquireService customerInquireService;

    @Override
    @Transactional
    public AccountTransactionDTO deposit(TransactionRequest transactionRequest)
            throws AccountNotFound, BusinessException {

        transactionValidator.ValidateDepositTransaction(transactionRequest);
        AccountDTO accountDTO = accountInquireService.getAccountById(transactionRequest.getAccountId());
        accountDTO.setBalance(accountDTO.getBalance().add(transactionRequest.getTransactionAmount()));

        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperContext(transactionRequest.getOperContext());
        accountUpdateRequest.setAccount(accountDTO);
        AccountDTO updatedAccountDTO = accountMaintService.updateAccount(accountUpdateRequest);

        //log transaction to transaction history
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTransactionDate(LocalDateTime.now());
        accountTransaction.setTransactionAmount(transactionRequest.getTransactionAmount());
        accountTransaction.setTransactionType("CREDIT");
        accountTransaction.setCustomerId(updatedAccountDTO.getCustomerId());
        accountTransaction.setAccountId(transactionRequest.getAccountId());
        accountTransaction.setAccountBalance(updatedAccountDTO.getBalance());
        accountTransaction.setDescription(transactionRequest.getDescription());
        transactionMapper.insertTransaction(accountTransaction);

        AccountTransactionDTO accountTransactionDTO = transactionConverter.transactionToDto(accountTransaction);
//        accountTransaction.setCreatedAt(LocalDateTime.now());

        //send to kafka for SMS/Email notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(updatedAccountDTO.getCustomerId());
        transactionNotification.sendTransactionNotification(accountTransactionDTO, customerDTO.getMobile(), customerDTO.getEmail());

        return accountTransactionDTO;

//        return transactionConverter.transactionToDto(accountTransaction);
    }

    @Override
    @Transactional
    public AccountTransactionDTO withdraw(TransactionRequest transactionRequest)
            throws AccountNotFound, BusinessException {

        AccountDTO accountDTO = accountInquireService.getAccountById(transactionRequest.getAccountId());
        transactionValidator.ValidateWithdrawTransaction(transactionRequest, accountDTO);
        accountDTO.setBalance(accountDTO.getBalance().subtract(transactionRequest.getTransactionAmount()));


        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperContext(transactionRequest.getOperContext());
        accountUpdateRequest.setAccount(accountDTO);
        AccountDTO updatedAccountDTO = accountMaintService.updateAccount(accountUpdateRequest);

        //log transaction to transaction history
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTransactionDate(LocalDateTime.now());
        accountTransaction.setTransactionAmount(transactionRequest.getTransactionAmount());
        accountTransaction.setTransactionType("DEBIT");
        accountTransaction.setCustomerId(updatedAccountDTO.getCustomerId());
        accountTransaction.setAccountId(transactionRequest.getAccountId());
        accountTransaction.setAccountBalance(updatedAccountDTO.getBalance());
        accountTransaction.setDescription(transactionRequest.getDescription());
        transactionMapper.insertTransaction(accountTransaction);

        AccountTransactionDTO accountTransactionDTO = transactionConverter.transactionToDto(accountTransaction);

        //send to kafka for SMS/Email notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(updatedAccountDTO.getCustomerId());
        transactionNotification.sendTransactionNotification(accountTransactionDTO, customerDTO.getMobile(), customerDTO.getEmail());

        return accountTransactionDTO;

    }

    @Override
    public List<AccountTransactionDTO> getTransactionHistory(Long accountId) throws AccountNotFound {
        // 验证账户存在
        accountInquireService.getAccountById(accountId);

        List<AccountTransaction> transactions = transactionMapper.getTransactionsByAccountId(accountId);
        return transactions.stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }
}