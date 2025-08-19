package com.simple.bank.service.kafka;

import com.simple.bank.message.TransactionMessage;
import com.simple.bank.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class TransactionMessageSMSConsumer {
    @Autowired
    SMSService smsService;

    @KafkaListener(topics = TransactionMessage.TOPIC,
            groupId = "consumer-group-sms" + TransactionMessage.TOPIC)
    public void onMessage(TransactionMessage transactionMessage) {
        log.info("[onMessage][thread:{} message：{}]", Thread.currentThread().getId(), transactionMessage);
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
