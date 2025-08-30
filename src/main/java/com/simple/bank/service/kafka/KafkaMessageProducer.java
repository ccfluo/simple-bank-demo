package com.simple.bank.service.kafka;

import com.simple.bank.message.KafkaPurchaseMessage;
import com.simple.bank.message.KafkaTransactionMessage;
import com.simple.bank.message.KafkaTransferMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    // 同步发送消息
    public SendResult syncSendTransactionMessage(KafkaTransactionMessage kafkaTransactionMessage) throws ExecutionException, InterruptedException{
          SendResult sendResult = kafkaTemplate.send(kafkaTransactionMessage.TOPIC, kafkaTransactionMessage).get();
          log.debug("[Transaction Message][transaction id：[{}]]", kafkaTransactionMessage.getTransactionId());
          return sendResult;
    }

    public void asyncSendTransactionMessage(KafkaTransactionMessage kafkaTransactionMessage) {
        CompletableFuture<SendResult<Object, Object>> completableFuture;

        completableFuture = kafkaTemplate.send(kafkaTransactionMessage.TOPIC, kafkaTransactionMessage);
        log.debug("[Transaction Message][transaction id：[{}]]", kafkaTransactionMessage.getTransactionId());

        //CompletableFuture:
        //   succ: return result(1st parm); fail: return throwable (2nd parm)
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Transaction Message][transaction id：[{}] send abnormal]", kafkaTransactionMessage.getTransactionId(), throwable);
            } else {
                log.info("[Transaction Message][transaction id：[{}] send successfully, result ：[{}]]", kafkaTransactionMessage.getTransactionId(), result);
            }
        });
    }

    public void asyncSendTransferMessage(KafkaTransferMessage kafkaTransferMessage) {
        CompletableFuture<SendResult<Object, Object>> completableFuture;

        completableFuture = kafkaTemplate.send(kafkaTransferMessage.TOPIC, kafkaTransferMessage);
        log.debug("[Transfer Message][transfer id：[{}]]", kafkaTransferMessage.getTransferId());

        //CompletableFuture:
        //   succ: return result(1st parm); fail: return throwable (2nd parm)
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Transfer Message][transfer id：[{}] send abnormal]", kafkaTransferMessage.getTransferId(), throwable);
            } else {
                log.info("[Transfer Message][transfer id：[{}] send successfully, result ：[{}]]", kafkaTransferMessage.getTransferId(), result);
            }
        });
    }

    public void asyncSendPurchaseMessage(KafkaPurchaseMessage kafkaPurchaseMessage) {
        CompletableFuture<SendResult<Object, Object>> completableFuture;

        completableFuture = kafkaTemplate.send(kafkaPurchaseMessage.TOPIC, kafkaPurchaseMessage);
        log.debug("[Purchase Message][purchase id：[{}]]", kafkaPurchaseMessage.getPurchaseId());

        //CompletableFuture:
        //   succ: return result(1st parm); fail: return throwable (2nd parm)
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Purchase Message][purchase id：[{}] send abnormal]", kafkaPurchaseMessage.getPurchaseId(), throwable);
            } else {
                log.info("[Purchase Message][purchase id：[{}] send successfully, result ：[{}]]", kafkaPurchaseMessage.getPurchaseId(), result);
            }
        });
    }
}