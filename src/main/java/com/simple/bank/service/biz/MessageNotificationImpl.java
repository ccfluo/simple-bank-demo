package com.simple.bank.service.biz;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.TransferEntity;
import com.simple.bank.message.PurchaseMessage;
import com.simple.bank.message.TransactionMessage;
import com.simple.bank.message.TransferMessage;
import com.simple.bank.service.mq.MessageQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MessageNotificationImpl implements MessageNotification{

    @Autowired
    MessageQueueProducer messageQueueProducer;
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

        messageQueueProducer.asyncSend(transactionMessage.TOPIC, transactionMessage);
    }

    @Override
    public void sendTransferNotification(TransferEntity transferEntity,
                                         CustomerDTO fromCustomerDTO,
                                         CustomerDTO toCustomerDTO,
                                         BigDecimal fromAccountBalance,
                                         BigDecimal toAccountBalance) {
        TransferMessage transferMessage = new TransferMessage();
        transferMessage.setTransferId(transferEntity.getTransferId());

        transferMessage.setFromCustomerId(fromCustomerDTO.getCustomerId());
        transferMessage.setFromAccountId(transferEntity.getFromAccountId());
        transferMessage.setFromAccountBalance(fromAccountBalance);
        transferMessage.setFromCustomerMobile(fromCustomerDTO.getMobile());
        transferMessage.setFromCustomerEmail(fromCustomerDTO.getEmail());

        transferMessage.setToCustomerId(toCustomerDTO.getCustomerId());
        transferMessage.setToAccountId(transferEntity.getToAccountId());
        transferMessage.setToAccountBalance(toAccountBalance);
        transferMessage.setToCustomerMobile(toCustomerDTO.getMobile());
        transferMessage.setToCustomerEmail(toCustomerDTO.getEmail());

        transferMessage.setTransferType(transferEntity.getTransactionType());
        transferMessage.setTransferAmount(transferEntity.getTransferAmount());
        transferMessage.setTransferTime(transferEntity.getTransferTime());

        messageQueueProducer.asyncSend(transferMessage.TOPIC, transferMessage);

    }

    @Override
    public void sendPurchaseNotification(ProductPurchaseEntity purchaseEntity,
                                         BigDecimal accountBalance,
                                         String mobile, String email) {
        PurchaseMessage purchaseMessage = new PurchaseMessage();
        purchaseMessage.setPurchaseId(purchaseEntity.getPurchaseId());
        purchaseMessage.setProductId(purchaseEntity.getProductId());
        purchaseMessage.setCustomerId(purchaseEntity.getCustomerId());
        purchaseMessage.setAccountId(purchaseEntity.getAccountId());
        purchaseMessage.setPurchaseAmount(purchaseEntity.getPurchaseAmount());
        purchaseMessage.setPurchaseTime(purchaseEntity.getPurchaseTime());
        purchaseMessage.setStatus(purchaseEntity.getStatus());
        purchaseMessage.setTransactionTraceId(purchaseEntity.getTransactionTraceId());
        purchaseMessage.setMobile(mobile);
        purchaseMessage.setEmail(email);
        purchaseMessage.setAccountBalance(accountBalance);

        messageQueueProducer.asyncSend(purchaseMessage.TOPIC, purchaseMessage);

    }
}


