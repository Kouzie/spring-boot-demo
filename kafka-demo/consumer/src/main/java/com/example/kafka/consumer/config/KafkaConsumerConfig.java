package com.example.kafka.consumer.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumer 모듈 공통 설정 관리
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${my.kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${my.kafka.consumer.username}")
    private String username;

    @Value("${my.kafka.consumer.password}")
    private String password;

    /**
     * Consumer 공통 설정 맵 생성 (group.id는 각 Consumer에서 설정)
     */
    public Map<String, Object> getConfigs() {
        Map<String, Object> configs = new HashMap<>();
        
        // 공통 설정
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        configs.put("sasl.mechanism", "PLAIN");
        configs.put("sasl.jaas.config", 
            "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"" + username + "\" password=\"" + password + "\";");
        
        // Consumer 전용 설정
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        return configs;
    }
}

