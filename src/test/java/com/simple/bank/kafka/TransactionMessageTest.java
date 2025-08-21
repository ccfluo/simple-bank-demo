package com.simple.bank.kafka;

import com.simple.bank.message.TransactionMessage;
import com.simple.bank.service.kafka.TransactionMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class TransactionMessageTest {
    //
    @Autowired
    private TransactionMessageProducer producer;

    @Test
    public void testSample() throws ExecutionException, InterruptedException {

        log.info("this is junit test");
    }

    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException {

        TransactionMessage transactionMessage = new TransactionMessage();

        transactionMessage.setTransactionId(Long.MAX_VALUE);
        transactionMessage.setCustomerId(Long.MAX_VALUE);
        transactionMessage.setAccountId(Long.MAX_VALUE);
        transactionMessage.setTransactionType("DEBIT");
        transactionMessage.setAmount(BigDecimal.ZERO);
        transactionMessage.setBalanceAfter(BigDecimal.ZERO);
        transactionMessage.setTransactionTime(LocalDateTime.now());
        transactionMessage.setMobile("########");
        transactionMessage.setEmail("XXX@XXX.COM");

        SendResult sendResult = producer.syncSend(transactionMessage);
        log.info("[testSyncSend][sending id：[{}] result：[{}]]", transactionMessage.getTransactionId(), sendResult);

        CountDownLatch latch = new CountDownLatch(1);
//        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();

        boolean isCompleted = latch.await(10, TimeUnit.SECONDS);
        if (!isCompleted) {
            log.warn("timeout，kafka message might not be consumed");
        }
    }
    //
    @Test
    public void testASyncSend() throws InterruptedException {
        TransactionMessage transactionMessage = new TransactionMessage();

        transactionMessage.setTransactionId(Long.MAX_VALUE);
        transactionMessage.setCustomerId(Long.MAX_VALUE);
        transactionMessage.setAccountId(Long.MAX_VALUE);
        transactionMessage.setTransactionType("DEBIT");
        transactionMessage.setAmount(BigDecimal.ZERO);
        transactionMessage.setBalanceAfter(BigDecimal.ZERO);
        transactionMessage.setTransactionTime(LocalDateTime.now());
        transactionMessage.setMobile("########");
        transactionMessage.setEmail("XXX@XXX.COM");

        producer.asyncSend(transactionMessage);

//        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();
        CountDownLatch latch = new CountDownLatch(1);
        boolean isCompleted = latch.await(10, TimeUnit.SECONDS);
        if (!isCompleted) {
            log.warn("timeout，kafka message might not be consumed");
        }



    }

}