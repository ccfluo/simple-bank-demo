package com.simple.bank.service.kafka;

import com.simple.bank.message.KafkaPurchaseMessage;
import com.simple.bank.message.KafkaTransactionMessage;
import com.simple.bank.message.KafkaTransferMessage;
import com.simple.bank.service.other.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class KafkaMessageSMSConsumer {
    @Autowired
    SMSService smsService;

    @KafkaListener(topics = KafkaTransactionMessage.TOPIC,
            groupId = "consumer-group-sms-transaction" + KafkaTransactionMessage.TOPIC)
    public void onMessage(KafkaTransactionMessage kafkaTransactionMessage) {
        log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), kafkaTransactionMessage);
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

        smsService.sendSms(kafkaTransactionMessage.getMobile(), content);
    }

    @KafkaListener(topics = KafkaTransferMessage.TOPIC,
            groupId = "consumer-group-sms-transfer" + KafkaTransferMessage.TOPIC)
    public void onTransferMessage(KafkaTransferMessage kafkaTransferMessage) {
        log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), kafkaTransferMessage);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedTransferAmount = decimalFormat.format(kafkaTransferMessage.getTransferAmount());
        String formattedFromAccountBalance = decimalFormat.format(kafkaTransferMessage.getFromAccountBalance());
        String formattedToAccountBalance = decimalFormat.format(kafkaTransferMessage.getToAccountBalance());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedTime = kafkaTransferMessage.getTransferTime().format(formatter);

        String fromContent = String.format("Your account %s has transfer out amount：%s，balance：%s",
                formattedTime,
                formattedTransferAmount,
                formattedFromAccountBalance);

        smsService.sendSms(kafkaTransferMessage.getFromCustomerMobile(), fromContent);

        String toContent = String.format("Your account %s has transfer in amount：%s，balance：%s",
                formattedTime,
                formattedTransferAmount,
                formattedToAccountBalance);

        smsService.sendSms(kafkaTransferMessage.getToCustomerMobile(), toContent);
    }

    @KafkaListener(topics = KafkaPurchaseMessage.TOPIC,
            groupId = "consumer-group-sms-purchase" + KafkaPurchaseMessage.TOPIC)
    public void onPurchaseMessage(KafkaPurchaseMessage kafkaPurchaseMessage) {
        log.debug("[onMessageSMS][thread:{} message：{}]", Thread.currentThread().getId(), kafkaPurchaseMessage);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedPurchaseAmount = decimalFormat.format(kafkaPurchaseMessage.getPurchaseAmount());
//        String formattedFromAccountBalance = decimalFormat.format(kafkaPurchaseMessage.getFromAccountBalance());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedTime = kafkaPurchaseMessage.getPurchaseTime().format(formatter);

        String fromContent = String.format("Your account %s has purchase product %s, amount：%s，balance：%s",
                formattedTime,
                kafkaPurchaseMessage.getProductId(),
                formattedPurchaseAmount,
                kafkaPurchaseMessage.getAccountBalance());

        smsService.sendSms(kafkaPurchaseMessage.getMobile(), fromContent);
    }
}
