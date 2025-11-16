package com.example.kafka.apache.client.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Properties;

@Configuration
public class MyKafkaConfig {
    @Value("${my.kafka.bootstrap.servers.config}")
    private String bootstrapServersConfig;
    @Value("${my.kafka.topic}")
    private String topic;
    @Value("${my.kafka.group.id.config}")
    private String groupId;
    @Value("${my.kafka.security.protocol:}")
    private String securityProtocol;
    @Value("${my.kafka.sasl.mechanism:}")
    private String saslMechanism;
    @Value("${my.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;
    
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        if (securityProtocol != null && !securityProtocol.isEmpty()) {
            configs.put("security.protocol", securityProtocol);
        }
        if (saslMechanism != null && !saslMechanism.isEmpty()) {
            configs.put("sasl.mechanism", saslMechanism);
        }
        if (saslJaasConfig != null && !saslJaasConfig.isEmpty()) {
            configs.put("sasl.jaas.config", saslJaasConfig);
        }
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList(topic));
        System.out.println(StringDeserializer.class.getName());
        return consumer;
    }
}

