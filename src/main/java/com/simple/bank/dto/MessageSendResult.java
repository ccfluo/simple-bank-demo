package com.simple.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendResult {

    // 消息是否发送成功
    private boolean success;
    // 消息ID
    private String messageId;
    // 发送时间
    private Instant sendTime;
    // 主题
    private String topic;
    // 分区(仅Kafka有)
    private Integer partition;
    // 偏移量(仅Kafka有)
    private Long offset;
    // 队列ID(仅RocketMQ有)
    private Integer queueId;
    // 原生长度结果对象(需要时可直接获取)
    private Object originalResult;
    // 错误信息(失败时)
    private String errorMessage;

}

