package com.simple.bank.service.kafka;

import com.simple.bank.message.KafkaPurchaseMessage;
import com.simple.bank.message.KafkaTransactionMessage;
import com.simple.bank.message.KafkaTransferMessage;
import com.simple.bank.service.other.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class KafkaMessageEmailConsumer {
    @Autowired
    EmailService emailService;

    @KafkaListener(topics = KafkaTransactionMessage.TOPIC,
            groupId = "consumer-group-email-transaction" + KafkaTransactionMessage.TOPIC)
    public void onTransactionMessage(KafkaTransactionMessage kafkaTransactionMessage) {
        log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), kafkaTransactionMessage);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedAmount = decimalFormat.format(kafkaTransactionMessage.getAmount());
        String formattedBalance = decimalFormat.format(kafkaTransactionMessage.getBalanceAfter());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedTime = kafkaTransactionMessage.getTransactionTime().format(formatter);

        String content = String.format("Your account %s has %s transaction，amount：%s，balance：%s",
                formattedTime,
                "CREDIT".equals(kafkaTransactionMessage.getTransactionType()) ? "deposit" : "withdrawal",
                formattedAmount,
                formattedBalance);

        emailService.sendEmail(kafkaTransactionMessage.getEmail(), content);
    }

    @KafkaListener(topics = KafkaTransferMessage.TOPIC,
            groupId = "consumer-group-email-transfer" + KafkaTransferMessage.TOPIC)
    public void onTransferMessage(KafkaTransferMessage kafkaTransferMessage) {
        log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), kafkaTransferMessage);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedTransferAmount = decimalFormat.format(kafkaTransferMessage.getTransferAmount());
        String formattedFromAccountBalance = decimalFormat.format(kafkaTransferMessage.getFromAccountBalance());
        String formattedToAccountBalance = decimalFormat.format(kafkaTransferMessage.getToAccountBalance());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedTime = kafkaTransferMessage.getTransferTime().format(formatter);

        String fromContent = String.format("Your account had RMB %s transferred out at %s，Current balance：RMB %s",
                formattedTransferAmount,
                formattedTime,
                formattedFromAccountBalance);

        emailService.sendEmail(kafkaTransferMessage.getFromCustomerEmail(), fromContent);

        String toContent = String.format("Your account received a transfer of RMB %s at %s，Current balance：RMB %s",
                formattedTransferAmount,
                formattedTime,
                formattedToAccountBalance);

        emailService.sendEmail(kafkaTransferMessage.getToCustomerEmail(), toContent);
    }

    @KafkaListener(topics = KafkaPurchaseMessage.TOPIC,
            groupId = "consumer-group-email-purchase" + KafkaPurchaseMessage.TOPIC)
    public void onPurchaseMessage(KafkaPurchaseMessage kafkaPurchaseMessage) {
        log.debug("[onMessageEmail][thread:{} message：{}]", Thread.currentThread().getId(), kafkaPurchaseMessage);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedPurchaseAmount = decimalFormat.format(kafkaPurchaseMessage.getPurchaseAmount());
//        String formattedFromAccountBalance = decimalFormat.format(kafkaPurchaseMessage.getFromAccountBalance());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedTime = kafkaPurchaseMessage.getPurchaseTime().format(formatter);

        String fromContent = String.format("Your account purchased product %s for RMB %s at %s. Current balance：RMB %s",
                kafkaPurchaseMessage.getProductId(),
                formattedPurchaseAmount,
                formattedTime,
                kafkaPurchaseMessage.getAccountBalance());

        emailService.sendEmail(kafkaPurchaseMessage.getEmail(), fromContent);

    }
}
