package com.simple.bank.service.mq.kafka;

import com.simple.bank.message.PurchaseMessage;
import com.simple.bank.message.TransactionMessage;
import com.simple.bank.message.TransferMessage;
import com.simple.bank.service.other.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class KafkaMessageEmailConsumer {
    @Autowired
    EmailService emailService;

    @KafkaListener(topics = TransactionMessage.TOPIC,
            groupId = "consumer-group-email-transaction",
            concurrency = "2")   //create 2 threads to consume the topic
    public void onTransactionMessage(List<TransactionMessage> transactionMessages) {
        for(TransactionMessage transactionMessage : transactionMessages) {
            log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), transactionMessage);
//            if (true) {
//                throw new RuntimeException("simulate consumer failed to test kafka retry function");
//            }
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String formattedAmount = decimalFormat.format(transactionMessage.getAmount());
            String formattedBalance = decimalFormat.format(transactionMessage.getBalanceAfter());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedTime = transactionMessage.getTransactionTime().format(formatter);

            String content = String.format("Your account %s has %s transaction，amount：%s，balance：%s",
                    formattedTime,
                    "CREDIT".equals(transactionMessage.getTransactionType()) ? "deposit" : "withdrawal",
                    formattedAmount,
                    formattedBalance);

            emailService.sendEmail(transactionMessage.getEmail(), content);
        }

    }

    @KafkaListener(topics = TransferMessage.TOPIC,
            groupId = "consumer-group-email-transfer")
    public void onTransferMessage(List<TransferMessage> transferMessages) {
        for (TransferMessage transferMessage : transferMessages) {
            log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), transferMessage);

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String formattedTransferAmount = decimalFormat.format(transferMessage.getTransferAmount());
            String formattedFromAccountBalance = decimalFormat.format(transferMessage.getFromAccountBalance());
            String formattedToAccountBalance = decimalFormat.format(transferMessage.getToAccountBalance());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedTime = transferMessage.getTransferTime().format(formatter);

            String fromContent = String.format("Your account had RMB %s transferred out at %s，Current balance：RMB %s",
                    formattedTransferAmount,
                    formattedTime,
                    formattedFromAccountBalance);

            emailService.sendEmail(transferMessage.getFromCustomerEmail(), fromContent);

            String toContent = String.format("Your account received a transfer of RMB %s at %s，Current balance：RMB %s",
                    formattedTransferAmount,
                    formattedTime,
                    formattedToAccountBalance);

            emailService.sendEmail(transferMessage.getToCustomerEmail(), toContent);
        }

    }

    @KafkaListener(topics = PurchaseMessage.TOPIC,
            groupId = "consumer-group-email-purchase")
    public void onPurchaseMessage(List<PurchaseMessage> purchaseMessages) {
        for (PurchaseMessage purchaseMessage : purchaseMessages) {
            log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), purchaseMessage);

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String formattedPurchaseAmount = decimalFormat.format(purchaseMessage.getPurchaseAmount());
//        String formattedFromAccountBalance = decimalFormat.format(kafkaPurchaseMessage.getFromAccountBalance());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedTime = purchaseMessage.getPurchaseTime().format(formatter);

            String fromContent = String.format("Your account purchased product %s for RMB %s at %s. Current balance：RMB %s",
                    purchaseMessage.getProductId(),
                    formattedPurchaseAmount,
                    formattedTime,
                    purchaseMessage.getAccountBalance());

            emailService.sendEmail(purchaseMessage.getEmail(), fromContent);
        }


    }
}
