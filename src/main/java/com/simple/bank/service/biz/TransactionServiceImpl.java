package com.simple.bank.service.biz;

import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.converter.TransactionConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.AccountEntity;
import com.simple.bank.entity.AccountTransaction;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.mapper.TransactionMapper;
import com.simple.bank.service.redis.RedissionLock;
import com.simple.bank.validator.TransactionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    private AccountMapper accountMapper;

    @Autowired
    private AccountMaintService accountMaintService;

    @Autowired
    private TransactionConverter transactionConverter;

    @Autowired
    private TransactionValidator transactionValidator;

    @Autowired
    private MessageNotification messageNotification;

    @Autowired
    private CustomerInquireService customerInquireService;

    @Autowired
    private RedissionLock redissionLock;

    @Override
    @Transactional
    public AccountTransactionDTO deposit(TransactionRequest transactionRequest) throws BusinessException {
//        return redissionLock.lock("transaction_lock:" + transactionRequest.getTransactionTraceId(), 500L, () -> doDeposit(transactionRequest));
        return redissionLock.lock("account:lock:" + transactionRequest.getAccountId(), 500L,
                () -> doDeposit(transactionRequest));
    }

    private AccountTransactionDTO doDeposit(TransactionRequest transactionRequest) throws BusinessException {
        AccountTransactionDTO accountTransactionDTO = creditAccountBalance(transactionRequest);

        //send to kafka for SMS/Email notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(accountTransactionDTO.getCustomerId());
        messageNotification.sendTransactionNotification(accountTransactionDTO, customerDTO.getMobile(), customerDTO.getEmail());

        return accountTransactionDTO;
    }

    @Override
    @Transactional
    public AccountTransactionDTO creditAccountBalance(TransactionRequest transactionRequest) throws BusinessException {

        AccountEntity accountEntity = accountMapper.getAccountByIdForUpdate(transactionRequest.getAccountId());
        transactionValidator.ValidateCreditAccountBalance(transactionRequest);

        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperContext(transactionRequest.getOperContext());
        // only update account balance, no need get all fields from accountEntity and update all others without change
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(accountEntity.getAccountId());
        accountDTO.setBalance(accountEntity.getBalance().add(transactionRequest.getTransactionAmount()));
        accountUpdateRequest.setAccount(accountDTO);

        AccountDTO updatedAccountDTO = accountMaintService.updateAccount(accountUpdateRequest);
        log.info("###phoebe: " + updatedAccountDTO);

        //log transaction to transaction history
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTransactionDate(LocalDateTime.now());
        accountTransaction.setTransactionAmount(transactionRequest.getTransactionAmount());
        accountTransaction.setTransactionType("CREDIT");
        accountTransaction.setCustomerId(updatedAccountDTO.getCustomerId());
        accountTransaction.setAccountId(transactionRequest.getAccountId());
        accountTransaction.setAccountBalance(updatedAccountDTO.getBalance());
        accountTransaction.setDescription(transactionRequest.getDescription());
        accountTransaction.setTransactionTraceId(transactionRequest.getTransactionTraceId());
        try {
            int result = transactionMapper.insertTransaction(accountTransaction);
            if (result < 1) {
                throw new BusinessException("DB_UPDATE_FAIL", "No record inserted, pls check backend");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Duplicate transaction");
        }

        AccountTransactionDTO accountTransactionDTO = transactionConverter.transactionToDto(accountTransaction);
        return accountTransactionDTO;

    }

    @Override
    @Transactional
    public AccountTransactionDTO withdraw(TransactionRequest transactionRequest) throws BusinessException {
//        return redissionLock.lock("transaction_lock:" + transactionRequest.getTransactionTraceId(), 500L, () -> doWithdraw(transactionRequest));
        return redissionLock.lock("account:lock:" + transactionRequest.getAccountId(), 500L,
                () -> doWithdraw(transactionRequest));
    }

    private AccountTransactionDTO doWithdraw(TransactionRequest transactionRequest) throws BusinessException {
        AccountTransactionDTO accountTransactionDTO = debitAccountBalance(transactionRequest);

        //send to kafka for SMS/Email notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(accountTransactionDTO.getCustomerId());
        messageNotification.sendTransactionNotification(accountTransactionDTO, customerDTO.getMobile(), customerDTO.getEmail());

        return accountTransactionDTO;
    }

    @Override
    @Transactional
    public AccountTransactionDTO debitAccountBalance(TransactionRequest transactionRequest) throws BusinessException {

        AccountEntity accountEntity = accountMapper.getAccountByIdForUpdate(transactionRequest.getAccountId());
        if (accountEntity == null) {
            throw new BusinessException("NOT_FOUND", "Account with id " + transactionRequest.getAccountId() + " not found");
        }

        transactionValidator.ValidateDebitAccountBalance(transactionRequest, accountEntity.getBalance());

        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperContext(transactionRequest.getOperContext());
        // only update account balance, no need get all fields from accountEntity and update all others without change
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(accountEntity.getAccountId());
        accountDTO.setBalance(accountEntity.getBalance());
        accountDTO.setBalance(accountDTO.getBalance().subtract(transactionRequest.getTransactionAmount()));
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
        accountTransaction.setTransactionTraceId(transactionRequest.getTransactionTraceId());
        try {
            int result = transactionMapper.insertTransaction(accountTransaction);
            if (result < 1) {
                throw new BusinessException("DB_UPDATE_FAIL", "No record inserted, pls check backend");
            }
        }catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Duplicate transaction");
        }

        AccountTransactionDTO accountTransactionDTO = transactionConverter.transactionToDto(accountTransaction);
        return accountTransactionDTO;

    }

    @Override
    public List<AccountTransactionDTO> getTransactionHistoryByAccountId(Long accountId) throws BusinessException {

        List<AccountTransaction> transactions = transactionMapper.getTransactionsByAccountId(accountId);
        return transactions.stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountTransactionDTO> getTransactionHistoryByCustomerId(Long customerId) throws BusinessException {

        List<AccountTransaction> transactions = transactionMapper.getTransactionsByCustomerId(customerId);
        return transactions.stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<AccountTransactionDTO> getTransactionBetween(LocalDateTime start, LocalDateTime end) throws BusinessException {

        List<AccountTransaction> transactions = transactionMapper.getTransactionsBetween(start, end);
        return transactions.stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }
}