package com.simple.bank.service.mq;

import com.simple.bank.dto.MessageSendResult;

import java.util.concurrent.ExecutionException;

public interface MessageQueueProducer {
    /**
     * 发送消息到指定主题
     * @param topic 主题名称
     * @param message 消息内容
     */

    MessageSendResult syncSend(String topic, Object message) throws ExecutionException, InterruptedException;

    void asyncSend(String topic, Object message);

    /**
     * 获取当前使用的消息队列类型
     * @return 消息队列类型名称
     */
    String getType();
}
