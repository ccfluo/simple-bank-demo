package com.simple.bank.service.mq.kafka;

import com.simple.bank.message.PurchaseMessage;
import com.simple.bank.message.TransactionMessage;
import com.simple.bank.message.TransferMessage;
import com.simple.bank.service.other.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class KafkaMessageSMSConsumer {
    @Autowired
    SMSService smsService;

    @KafkaListener(topics = TransactionMessage.TOPIC,
            groupId = "consumer-group-sms-transaction")
    public void onMessage(List<TransactionMessage> kafkaTransactionMessages) {
        for(TransactionMessage transactionMessage : kafkaTransactionMessages ){
            log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), transactionMessage);
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

            smsService.sendSms(transactionMessage.getMobile(), content);
        }

    }

    @KafkaListener(topics = TransferMessage.TOPIC,
            groupId = "consumer-group-sms-transfer")
    public void onTransferMessage(List<TransferMessage> transferMessages) {
        for(TransferMessage transferMessage : transferMessages) {
            log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), transferMessage);

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String formattedTransferAmount = decimalFormat.format(transferMessage.getTransferAmount());
            String formattedFromAccountBalance = decimalFormat.format(transferMessage.getFromAccountBalance());
            String formattedToAccountBalance = decimalFormat.format(transferMessage.getToAccountBalance());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedTime = transferMessage.getTransferTime().format(formatter);

            String fromContent = String.format("Your account had RMB %s transferred out at %s. Current balance：RMB %s",
                    formattedTransferAmount,
                    formattedTime,
                    formattedFromAccountBalance);

            smsService.sendSms(transferMessage.getFromCustomerMobile(), fromContent);

            String toContent = String.format("Your account received a transfer of RMB %s at %s. Current balance：RMB %s",
                    formattedTransferAmount,
                    formattedTime,
                    formattedToAccountBalance);

            smsService.sendSms(transferMessage.getToCustomerMobile(), toContent);
        }

    }

    @KafkaListener(topics = PurchaseMessage.TOPIC,
            groupId = "consumer-group-sms-purchase")
    public void onPurchaseMessage(List<PurchaseMessage> purchaseMessages) {
        for (PurchaseMessage purchaseMessage : purchaseMessages) {
            log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), purchaseMessage);

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

            smsService.sendSms(purchaseMessage.getMobile(), fromContent);
        }

    }
}
