package com.example.kafka.producer.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.admin.AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG;

/**
 * Producer 모듈 공통 설정 관리
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${my.kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${my.kafka.producer.username:producer}")
    private String username;

    @Value("${my.kafka.producer.password:producer-secret}")
    private String password;

    /**
     * Producer 공통 설정 맵 생성
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

        // Producer 전용 설정
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.ACKS_CONFIG, "1");

        return configs;
    }

    @Bean
    public AdminClient adminClient() {
        // AdminClient는 producerConfigs를 기반으로 하되 admin 인증 정보 사용
        Map<String, Object> conf = new HashMap<>(getConfigs());
        // AdminClient 전용 설정 추가
        conf.put(REQUEST_TIMEOUT_MS_CONFIG, "5000");

        return AdminClient.create(conf);
    }
}

