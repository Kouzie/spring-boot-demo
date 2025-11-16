package com.example.kafka.consumer.config.impl;

import com.example.kafka.consumer.config.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Profile("apache")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApacheKafkaConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;

    // test1 토픽 Consumer (auto commit)
    @Bean(name = "test1KafkaConsumer")
    public KafkaConsumer<String, String> test1KafkaConsumer() {
        Map<String, Object> configs = new HashMap<>(kafkaConsumerConfig.getConfigs());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "test1-group");
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 자동 커밋
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList("test1"));
        log.info("Test1 KafkaConsumer Bean 생성 완료 - topic: test1, groupId: test1-group, autoCommit: true");
        return consumer;
    }

    // test2 토픽 Consumer (수동 커밋)
    @Bean(name = "test2KafkaConsumer")
    public KafkaConsumer<String, String> test2KafkaConsumer() {
        Map<String, Object> configs = new HashMap<>(kafkaConsumerConfig.getConfigs());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "test2-group");
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동 커밋
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList("test2"));
        log.info("Test2 KafkaConsumer Bean 생성 완료 - topic: test2, groupId: test2-group, autoCommit: false");
        return consumer;
    }
}

