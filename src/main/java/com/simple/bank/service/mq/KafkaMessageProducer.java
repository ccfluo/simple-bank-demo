package com.simple.bank.service.mq;

import com.simple.bank.dto.MessageSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Kafka消息服务实现类
 */
@Slf4j
@Service
public class KafkaMessageProducer implements MessageQueueProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    /**
     * 构造函数注入KafkaTemplate
     * @param kafkaTemplate Spring Kafka提供的模板类，用于操作Kafka
     */
    public KafkaMessageProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

//    @Override
//    public void sendMessage(String topic, String message) {
//        // 使用KafkaTemplate发送消息
//        kafkaTemplate.send(topic, message);
//    }

    @Override
    public MessageSendResult syncSend(String topic, Object message) throws ExecutionException, InterruptedException {
       SendResult sendResult = kafkaTemplate.send(topic, message).get();
       MessageSendResult messageSendResult = new MessageSendResult();

        messageSendResult.setSuccess(true);
        messageSendResult.setMessageId(sendResult.getRecordMetadata().toString());
        messageSendResult.setSendTime(Instant.now());
        messageSendResult.setTopic(topic);
        messageSendResult.setPartition(sendResult.getRecordMetadata().partition());
        messageSendResult.setOffset(sendResult.getRecordMetadata().offset());
        messageSendResult.setOriginalResult(sendResult); // save original kafka result
        return messageSendResult;
    }


    @Override
    public void asyncSend(String topic, Object message) {
        CompletableFuture<SendResult<Object, Object>> completableFuture;
        completableFuture = kafkaTemplate.send(topic, message);

        log.debug("");

        //CompletableFuture:
        //   succ: return result(1st parm); fail: return throwable (2nd parm)
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("[Async Send] [Message sent abnormal] ", throwable);
            } else {
                log.info("[Async send] [Message send successfully，result: [{}]]", result);
            }
        });

    }

    @Override
    public String getType() {
        return "Kafka";
    }
}
