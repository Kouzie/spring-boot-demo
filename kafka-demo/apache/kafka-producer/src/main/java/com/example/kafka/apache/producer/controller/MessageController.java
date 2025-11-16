package com.example.kafka.apache.producer.controller;

import com.example.kafka.apache.common.config.ProducerCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducer<String, String> producer;

    @Value("${my.kafka.topic}")
    private String topic;

    @PostMapping
    public String sendMessage(@RequestBody String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record, new ProducerCallback());
        log.info("메시지 발행 요청: {}", message);
        return "메시지가 발행되었습니다: " + message;
    }

    @PostMapping("/{key}")
    public String sendMessageWithKey(@PathVariable String key, @RequestBody String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        producer.send(record, new ProducerCallback());
        log.info("메시지 발행 요청 (key: {}): {}", key, message);
        return "메시지가 발행되었습니다 (key: " + key + "): " + message;
    }
}

