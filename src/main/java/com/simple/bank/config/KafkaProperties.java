package com.simple.bank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private List<String> bootstrapServers;
    private KafkaConsumerProperties consumer;

    // 判断Kafka是否已配置
    public boolean isConfigured() {
        return bootstrapServers != null && !bootstrapServers.isEmpty();
    }

    // getter和setter
    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaConsumerProperties getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumerProperties consumer) {
        this.consumer = consumer;
    }

    // 内部类：消费者配置
    public static class KafkaConsumerProperties {
        private String groupId;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
