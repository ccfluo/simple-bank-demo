package com.simple.bank.service.biz;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.TransferEntity;
import com.simple.bank.message.KafkaPurchaseMessage;
import com.simple.bank.message.KafkaTransactionMessage;
import com.simple.bank.message.KafkaTransferMessage;
import com.simple.bank.service.kafka.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class MessageNotificationImpl implements MessageNotification {

    @Autowired
    KafkaMessageProducer kafkaMessageProducer;
    public void sendTransactionNotification(AccountTransactionDTO accountTransactionDTO, String mobile, String email) {
        KafkaTransactionMessage kafkaTransactionMessage = new KafkaTransactionMessage();
        kafkaTransactionMessage.setTransactionId(accountTransactionDTO.getTransactionId());
        kafkaTransactionMessage.setAccountId(accountTransactionDTO.getAccountId());
        kafkaTransactionMessage.setTransactionType(accountTransactionDTO.getTransactionType());
        kafkaTransactionMessage.setAmount(accountTransactionDTO.getTransactionAmount());
        kafkaTransactionMessage.setBalanceAfter(accountTransactionDTO.getAccountBalance());
        kafkaTransactionMessage.setTransactionTime(accountTransactionDTO.getTransactionDate());
        kafkaTransactionMessage.setMobile(mobile);
        kafkaTransactionMessage.setEmail(email);

        kafkaMessageProducer.asyncSendTransactionMessage(kafkaTransactionMessage);
    }

    @Override
    public void sendTransferNotification(TransferEntity transferEntity,
                                         CustomerDTO fromCustomerDTO,
                                         CustomerDTO toCustomerDTO,
                                         BigDecimal fromAccountBalance,
                                         BigDecimal toAccountBalance) {
        KafkaTransferMessage kafkaTransferMessage = new KafkaTransferMessage();
        kafkaTransferMessage.setTransferId(transferEntity.getTransferId());

        kafkaTransferMessage.setFromCustomerId(fromCustomerDTO.getCustomerId());
        kafkaTransferMessage.setFromAccountId(transferEntity.getFromAccountId());
        kafkaTransferMessage.setFromAccountBalance(fromAccountBalance);
        kafkaTransferMessage.setFromCustomerMobile(fromCustomerDTO.getMobile());
        kafkaTransferMessage.setFromCustomerEmail(fromCustomerDTO.getEmail());

        kafkaTransferMessage.setToCustomerId(toCustomerDTO.getCustomerId());
        kafkaTransferMessage.setToAccountId(transferEntity.getToAccountId());
        kafkaTransferMessage.setToAccountBalance(toAccountBalance);
        kafkaTransferMessage.setToCustomerMobile(toCustomerDTO.getMobile());
        kafkaTransferMessage.setToCustomerEmail(toCustomerDTO.getEmail());

        kafkaTransferMessage.setTransferType(transferEntity.getTransactionType());
        kafkaTransferMessage.setTransferAmount(transferEntity.getTransferAmount());
        kafkaTransferMessage.setTransferTime(transferEntity.getTransferTime());

        kafkaMessageProducer.asyncSendTransferMessage(kafkaTransferMessage);

    }

    @Override
    public void sendPurchaseNotification(ProductPurchaseEntity purchaseEntity,
                                         BigDecimal accountBalance,
                                         String mobile, String email) {
        KafkaPurchaseMessage kafkaPurchaseMessage = new KafkaPurchaseMessage();
        kafkaPurchaseMessage.setPurchaseId(purchaseEntity.getPurchaseId());
        kafkaPurchaseMessage.setProductId(purchaseEntity.getProductId());
        kafkaPurchaseMessage.setCustomerId(purchaseEntity.getCustomerId());
        kafkaPurchaseMessage.setAccountId(purchaseEntity.getAccountId());
        kafkaPurchaseMessage.setPurchaseAmount(purchaseEntity.getPurchaseAmount());
        kafkaPurchaseMessage.setPurchaseTime(purchaseEntity.getPurchaseTime());
        kafkaPurchaseMessage.setStatus(purchaseEntity.getStatus());
        kafkaPurchaseMessage.setTransactionTraceId(purchaseEntity.getTransactionTraceId());
        kafkaPurchaseMessage.setMobile(mobile);
        kafkaPurchaseMessage.setEmail(email);
        kafkaPurchaseMessage.setAccountBalance(accountBalance);

        kafkaMessageProducer.asyncSendPurchaseMessage(kafkaPurchaseMessage);

    }
}


