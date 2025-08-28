package com.simple.bank.redis;


import com.simple.bank.api.request.AccountUpdateRequest;
import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.OperContext;
import com.simple.bank.entity.AccountTransaction;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.AccountMapper;
import com.simple.bank.mapper.TransactionMapper;
import com.simple.bank.service.biz.AccountMaintService;
import com.simple.bank.service.biz.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@SpringBootTest
public class TransactionTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountMaintService accountMaintService;

    @Test
    void testDepositTransaction() throws InterruptedException {
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        // initialize account balance as 100
        OperContext operContext = new OperContext();
        operContext.setUserId("testid");
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(28L);
        accountDTO.setBalance(BigDecimal.valueOf(100));
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setAccount(accountDTO);
        accountUpdateRequest.setOperContext(operContext);
        AccountDTO updatedAccountDTO= accountMaintService.updateAccount(accountUpdateRequest);

        // 5 concurrent requests to simulate duplicate deposit
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<AccountTransactionDTO> results = Collections.synchronizedList(new ArrayList<>());

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperContext(operContext);
        transactionRequest.setTransactionAmount(BigDecimal.valueOf(10));
        transactionRequest.setAccountId(28L);
        transactionRequest.setDescription("this is test transaction");
        transactionRequest.setTransactionTraceId("testTrace#"+System.currentTimeMillis());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    AccountTransactionDTO accountTransactionDTO = transactionService.deposit(transactionRequest);
                    results.add(accountTransactionDTO);
                } catch (Throwable e) {
                    if (e instanceof BusinessException
                            && e.getMessage() != null &&
                            (e.getMessage().contains("processed by other transaction") ||
                                    e.getMessage().contains("Transaction is processing"))
                            ) {
                        // doesn't get token as per expectation. ignore
                        log.info("thread didn't get the lock，ignore: {}", e.getMessage());
                    } else {
                        exceptions.add(e);
                    }
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 如果有异常，直接 fail
        if (!exceptions.isEmpty()) {
            exceptions.forEach(Throwable::printStackTrace);
            Assertions.fail("thread expectation error: " + exceptions.get(0).getMessage());
        }

        // 断言：只有一次 SUCCESS
        long successCount = results.stream().filter(r -> r.getAccountBalance() !=null).count();
        Assertions.assertEquals(1, successCount, "only 1 transaction processed");

        // 断言：流水表只有一条记录
        AccountTransaction accountTransaction = transactionMapper.getTransactionsByTraceId(transactionRequest.getTransactionTraceId());
        Assertions.assertNotNull(accountTransaction, "transaction history existing");

        // 断言：余额只减少一次
        BigDecimal finalBalance = accountMapper.getAccountById(transactionRequest.getAccountId()).getBalance();
        Assertions.assertTrue(
                finalBalance.compareTo(BigDecimal.valueOf(110)) == 0,
                "Balance expectation= " + BigDecimal.valueOf(110) + "，actual= " + finalBalance
        );
    }

    @Test
    void testWithdrawTransaction() throws InterruptedException {
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        // initialize account balance as 100
        OperContext operContext = new OperContext();
        operContext.setUserId("testid");
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountId(28L);
        accountDTO.setBalance(BigDecimal.valueOf(100));
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setAccount(accountDTO);
        accountUpdateRequest.setOperContext(operContext);
        AccountDTO updatedAccountDTO= accountMaintService.updateAccount(accountUpdateRequest);

        // 5 concurrent requests to simulate duplicate withdrawal
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<AccountTransactionDTO> results = Collections.synchronizedList(new ArrayList<>());

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperContext(operContext);
        transactionRequest.setTransactionAmount(BigDecimal.valueOf(10));
        transactionRequest.setAccountId(28L);
        transactionRequest.setDescription("this is test transaction");
        transactionRequest.setTransactionTraceId("testTrace#"+System.currentTimeMillis());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    AccountTransactionDTO accountTransactionDTO = transactionService.withdraw(transactionRequest);
                    results.add(accountTransactionDTO);
                } catch (Throwable e) {
                    if (e instanceof BusinessException
                            && e.getMessage() != null &&
                            (e.getMessage().contains("processed by other transaction") ||
                                    e.getMessage().contains("Transaction is processing"))
                    ) {
                        // doesn't get token as per expectation. ignore
                        log.info("thread didn't get the lock，ignore: {}", e.getMessage());
                    } else {
                        exceptions.add(e);
                    }
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 如果有异常，直接 fail
        if (!exceptions.isEmpty()) {
            exceptions.forEach(Throwable::printStackTrace);
            Assertions.fail("thread expectation error: " + exceptions.get(0).getMessage());
        }

        // 断言：只有一次 SUCCESS
        long successCount = results.stream().filter(r -> r.getAccountBalance() !=null).count();
        Assertions.assertEquals(1, successCount, "only one transaction processed");

        // 断言：流水表只有一条记录
        AccountTransaction accountTransaction = transactionMapper.getTransactionsByTraceId(transactionRequest.getTransactionTraceId());
        Assertions.assertNotNull(accountTransaction, "transaction history existing");

        // 断言：余额只减少一次
        BigDecimal finalBalance = accountMapper.getAccountById(transactionRequest.getAccountId()).getBalance();
//        Assertions.assertEquals(new BigDecimal("110.00"), finalBalance, "余额应只减少一次");
        Assertions.assertTrue(
                finalBalance.compareTo(BigDecimal.valueOf(90)) == 0,
                "Balance Expectation= " + BigDecimal.valueOf(90) + "，actual= " + finalBalance
        );
    }
}
