package com.example.kafka.producer.config.Impl;

import com.example.kafka.producer.config.KafkaProducerConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

@Profile("apache")
@Configuration
@RequiredArgsConstructor
public class ApacheKafkaProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;

    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        // Map을 Properties로 변환
        Properties configs = new Properties();
        configs.putAll(kafkaProducerConfig.getConfigs());
        
        return new KafkaProducer<>(configs);
    }
}

