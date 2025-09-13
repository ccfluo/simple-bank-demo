package com.simple.bank.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class KafkaConfiguration {

    private static final List<String> TOPIC_NAMES = Arrays.asList(
            "transaction_topic",
            "purchase_topic",
            "transfer_topic"
    );

    /**
     * initialize Kafka topic metadata:
     *  - avoid first msg delay: kafka need to know topic metadata before sending msg
     *  - verify kafka availability
     */
    @Bean
    public ApplicationRunner kafkaMetadataInitializer(KafkaTemplate<?, ?> kafkaTemplate) {
        return args -> {
//            Map<String, Object> producerConfig = kafkaTemplate.getProducerFactory().getConfigurationProperties();
//            System.out.println("metadata.fetch.timeout.ms：" + producerConfig.get("metadata.fetch.timeout.ms"));
//            System.out.println("socket.connection.setup.timeout.ms：" + producerConfig.get("socket.connection.setup.timeout.ms"));
            try {
                // trigger to get kafka topic metadata
                // partitionsFor method will wait until get metadata or timeout (set in application.yml)
                for(String topic: TOPIC_NAMES) {
                    kafkaTemplate.getProducerFactory().createProducer()
                            .partitionsFor(topic);
                    log.info("[Kafka initialization] initialize metadata for topic [" + topic + "] initialized successfully");
                }

            } catch (Exception e) {
                // failure handling: if Kafka not started
                log.error("[Kafka initialization] Failed to initialize Kafka metadata topic, error: " + e.getMessage());
                // throw new RuntimeException("Kafka metadata initialization failed", e);
            }
        };
    }

    @Bean
    public DefaultErrorHandler  kafkaErrorHandler(KafkaTemplate<?, ?> template) {
        // <1> create DeadLetterPublishingRecoverer class  dead letter topic: topic_name-dlt
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        // <2> create FixedBackOff class - retry 3 times with interval 10s
        BackOff backOff = new FixedBackOff(10 * 1000L, 3L);
        // <3> create DefaultErrorHandler class
        return new DefaultErrorHandler(recoverer, backOff);
    }
}
