package com.simple.bank.service.biz;

import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.api.request.TransferRequest;
import com.simple.bank.config.ApplicationConfig;
import com.simple.bank.converter.TransferConverter;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.TransferDTO;
import com.simple.bank.entity.TransferEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.mapper.TransferMapper;
import com.simple.bank.service.redis.RedissionLock;
import com.simple.bank.validator.TransferValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {
// directly load 1 parameter from application.yml
//    @Value("${simple.bank.lock.enableLockforTransfer}")
//    private boolean enableLockforTransfer;
    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private TransferValidator transferValidator;

    @Autowired
    private TransferConverter transferConverter;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerInquireService customerInquireService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RedissionLock redissionLock;

    @Autowired
    MessageNotification messageNotification;

    @Override
    @Transactional
    public TransferDTO transfer(TransferRequest request) throws BusinessException {
        // 1. Basic input validation
        transferValidator.validateBasicInput(request);

        if (applicationConfig.getLock().isEnableLockforTransfer()) {
            log.info("lock enabled for transfer");
            // 2. lock from/to account (smaller one first, then bigger one) - avoid deadlock
            //    execute core transfer logic - doTransfer
            return redissionLock.lockTwoAccounts(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    500L, // 最多等待500ms
                    () -> doTransfer(request) // 加锁后执行的业务逻辑
            );
        } else {
            log.info("lock disabled for transfer");
            return (doTransfer(request));
        }
    }

    public TransferDTO doTransfer(TransferRequest request) {
        // 1. validate request
        transferValidator.validate(request);

        // 2. transfer (debit from account, credit to account)
        AccountTransactionDTO fromAccountTransactionDTO = debitBalanceFromAccount(request);
        AccountTransactionDTO toAccountTransactionDTO =creditBalanceToAccount(request);

        // 3. log transfer history
        TransferEntity transferEntity = createTransferHistory(request);

        // 4. send SMS/Email notification to from/to customer
        CustomerDTO fromCustomerDTO = customerInquireService.getCustomerById(fromAccountTransactionDTO.getCustomerId());
        CustomerDTO toCustomerDTO = customerInquireService.getCustomerById(toAccountTransactionDTO.getCustomerId());
        messageNotification.sendTransferNotification(transferEntity,
                fromCustomerDTO, toCustomerDTO,
                fromAccountTransactionDTO.getAccountBalance(), toAccountTransactionDTO.getAccountBalance());

        TransferDTO transferDTO = transferConverter.transferToDto(transferEntity);
        return (transferDTO);
    }

    private AccountTransactionDTO debitBalanceFromAccount(TransferRequest request) throws BusinessException {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperContext(request.getOperContext());
        transactionRequest.setTransactionAmount(request.getTransferAmount());
        transactionRequest.setAccountId(request.getFromAccountId());
        transactionRequest.setDescription("transfer to " + request.getToAccountId());
        transactionRequest.setTransactionTraceId(request.getTransferTraceId());

        AccountTransactionDTO accountTransactionDTO = transactionService.debitAccountBalance(transactionRequest);
        return accountTransactionDTO;
    }

    private AccountTransactionDTO creditBalanceToAccount(TransferRequest request) throws BusinessException {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperContext(request.getOperContext());
        transactionRequest.setTransactionAmount(request.getTransferAmount());
        transactionRequest.setAccountId(request.getToAccountId());
        transactionRequest.setDescription("transfer from " + request.getFromAccountId());
        transactionRequest.setTransactionTraceId(request.getTransferTraceId());

        AccountTransactionDTO accountTransactionDTO = transactionService.creditAccountBalance(transactionRequest);
        return accountTransactionDTO;
    }

    private TransferEntity createTransferHistory(TransferRequest request) {
        TransferEntity transferEntity = new TransferEntity();
        transferEntity.setTransferTraceId(request.getTransferTraceId());
        transferEntity.setFromAccountId(request.getFromAccountId());
        transferEntity.setToAccountId(request.getToAccountId());
        transferEntity.setTransferAmount(request.getTransferAmount());
        transferEntity.setTransactionType("TRANSFER");
        transferEntity.setStatus("CLOSED");
        transferEntity.setRemark(request.getRemark());
        transferEntity.setTransferTime(LocalDateTime.now());
        transferMapper.insert(transferEntity);
        return transferEntity;

    }
}


