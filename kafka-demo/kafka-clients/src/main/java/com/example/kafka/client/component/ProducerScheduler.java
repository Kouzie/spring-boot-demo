package com.example.kafka.client.component;

import com.example.kafka.client.config.ProducerCallback;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerScheduler {
    @Value("${my.kafka.topic}")
    private String topic;

    private final KafkaProducer<String, String> producer;

    private Long index = 0l;

    @Scheduled(fixedDelay = 1000) // 10초마다 실행
    public void producerSchedule() {
        String data = "This is record " + index++;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, data);
        producer.send(record, new ProducerCallback());
    }
}
