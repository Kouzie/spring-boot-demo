package com.example.kafka.consumer.config.impl;

import com.example.kafka.consumer.config.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Profile("spring")
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class SpringKafkaConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig.getConfigs());
    }


    // 기본 Factory: RECORD 모드 (containerFactory 지정 없을 때 사용)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    // Factory 4: MANUAL 모드 (수동 커밋)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> manualKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 수동 커밋 (Acknowledgment 파라미터 필요)
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH); 성능 업그레이드
        return factory;
    }
}

