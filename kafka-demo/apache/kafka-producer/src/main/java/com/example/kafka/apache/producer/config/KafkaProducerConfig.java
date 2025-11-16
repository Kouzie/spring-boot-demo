package com.example.kafka.apache.producer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {
    @Value("${my.kafka.bootstrap.servers.config}")
    private String bootstrapServersConfig;

    @Value("${my.kafka.security.protocol:}")
    private String securityProtocol;
    
    @Value("${my.kafka.sasl.mechanism:}")
    private String saslMechanism;
    
    @Value("${my.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;

    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.ACKS_CONFIG, "1");
        
        if (securityProtocol != null && !securityProtocol.isEmpty()) {
            configs.put("security.protocol", securityProtocol);
        }
        if (saslMechanism != null && !saslMechanism.isEmpty()) {
            configs.put("sasl.mechanism", saslMechanism);
        }
        if (saslJaasConfig != null && !saslJaasConfig.isEmpty()) {
            configs.put("sasl.jaas.config", saslJaasConfig);
        }
        
        return new KafkaProducer<>(configs);
    }
}

