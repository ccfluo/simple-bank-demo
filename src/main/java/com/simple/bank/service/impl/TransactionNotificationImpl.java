package com.simple.bank.service.impl;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.message.TransactionMessage;
import com.simple.bank.service.TransactionNotification;
import com.simple.bank.service.kafka.TransactionMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionNotificationImpl implements TransactionNotification {

    @Autowired
    TransactionMessageProducer transactionMessageProducer;
    public void sendTransactionNotification(AccountTransactionDTO accountTransactionDTO, String mobile, String email) {
        TransactionMessage transactionMessage = new TransactionMessage();
        transactionMessage.setTransactionId(accountTransactionDTO.getTransactionId());
        transactionMessage.setAccountId(accountTransactionDTO.getAccountId());
        transactionMessage.setTransactionType(accountTransactionDTO.getTransactionType());
        transactionMessage.setAmount(accountTransactionDTO.getTransactionAmount());
        transactionMessage.setBalanceAfter(accountTransactionDTO.getAccountBalance());
        transactionMessage.setTransactionTime(accountTransactionDTO.getTransactionDate());
        transactionMessage.setMobile(mobile);
        transactionMessage.setEmail(email);

        transactionMessageProducer.asyncSend(transactionMessage);
    }
}
