package com.example.kafka.client.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MyKafkaConfig {
    @Value("${my.kafka.bootstrap.servers.config}")
    private String bootstrapServersConfig;
    @Value("${my.kafka.topic}")
    private String topic;
    @Value("${my.kafka.group.id.config}")
    private String groupId;
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig); // bootstrap.servers
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // group.id
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // key.deserializer
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value.deserializer
        // org.apache.kafka.common.serialization.StringDeserializer
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // auto.offset.reset
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // enable.auto.commit
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList(topic));
        System.out.println(StringDeserializer.class.getName());
        return consumer;
    }
    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig); // bootstrap.servers
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key.deserializer
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // value.deserializer
        configs.put(ProducerConfig.ACKS_CONFIG, "1"); // acks

        // configs.put(ProducerConfig.ACKS_CONFIG, "0");
        // configs.put(ProducerConfig.ACKS_CONFIG, "all");
        KafkaProducer<String, String> producer = new KafkaProducer<>(configs);
        return producer;
    }

    @Bean
    public AdminClient kafkaConfig() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        conf.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        AdminClient client = AdminClient.create(conf);
        return client;
    }
}
