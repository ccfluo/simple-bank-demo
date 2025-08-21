package com.simple.bank.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;


@Configuration
public class KafkaMetadataConfig {

    private static final String TOPIC_NAME = "transaction_topic";

    /**
     * 应用启动时初始化Kafka主题元数据
     */
    @Bean
    public ApplicationRunner kafkaMetadataInitializer(KafkaTemplate<?, ?> kafkaTemplate) {
        return args -> {
//            Map<String, Object> producerConfig = kafkaTemplate.getProducerFactory().getConfigurationProperties();
//            System.out.println("metadata.fetch.timeout.ms：" + producerConfig.get("metadata.fetch.timeout.ms"));
//            System.out.println("socket.connection.setup.timeout.ms：" + producerConfig.get("socket.connection.setup.timeout.ms"));
            try {
                // 主动获取主题元数据（触发元数据同步）
                // partitionsFor方法会阻塞直到获取到元数据或超时
                kafkaTemplate.getProducerFactory().createProducer()
                        .partitionsFor(TOPIC_NAME);

                // 初始化成功日志
                System.out.println("Kafka metadata for topic [" + TOPIC_NAME + "] initialized successfully");
            } catch (Exception e) {
                // 初始化失败处理（如Kafka未启动）
                System.err.println("Failed to initialize Kafka metadata for topic [" + TOPIC_NAME + "], error: " + e.getMessage());
                // throw new RuntimeException("Kafka metadata initialization failed", e);
            }
        };
    }
}
