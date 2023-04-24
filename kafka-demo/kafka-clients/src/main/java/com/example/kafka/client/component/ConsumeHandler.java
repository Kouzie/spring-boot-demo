package com.example.kafka.client.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumeHandler {

    private final KafkaConsumer<String, String> consumer;

    @PostConstruct
    private void init() {
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    log.info(record.toString());
                }
                consumer.commitAsync();
            }
        }).start();
    }
}
