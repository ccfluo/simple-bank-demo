package com.simple.bank.service.kafka;

import com.simple.bank.message.TransactionMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class TransactionMessageProducer {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

//    public SendResult syncSend(TransactionMessage transactionMessage) throws ExecutionException, InterruptedException {
        // 同步发送消息
      public void syncSend(TransactionMessage transactionMessage) {
          try {
              SendResult sendResult = kafkaTemplate.send(transactionMessage.TOPIC, transactionMessage).get();
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          } catch (ExecutionException e) {
              throw new RuntimeException(e);
          }
          log.info("[Transaction Message][transaction id：[{}]]", transactionMessage.getTransactionId());

          //TODO: Phoebe add logic to output sendResult
//        return sendResult;
    }

//    public CompletableFuture<SendResult<Object, Object>> asyncSend(TransactionMessage transactionMessage) {
    public void asyncSend(TransactionMessage transactionMessage) {
        CompletableFuture<SendResult<Object, Object>> completableFuture;

        completableFuture = kafkaTemplate.send(transactionMessage.TOPIC, transactionMessage);
        log.info("[Transaction Message][transaction id：[{}]]", transactionMessage.getTransactionId());

        //CompletableFuture:
        //   succ: return result(1st parm); fail: return throwable (2nd parm)
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Transaction Message][transaction id：[{}] send abnormal]", transactionMessage.getTransactionId(), throwable);
            } else {
                log.info("[Transaction Message][transaction id：[{}] send successfully, result ：[{}]]", transactionMessage.getTransactionId(), result);
            }
        });
    }

}