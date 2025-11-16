package com.example.kafka.producer.component.impl;

import com.example.kafka.producer.component.AbstractMessagePublisher;
import com.example.kafka.producer.dto.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Spring Kafka 프로필용 메시지 발행자
 * 템플릿 메서드 패턴을 사용하여 발행 로직만 구현
 * Future를 사용하지 않고 동기적으로 처리
 */
@Profile("spring")
@Slf4j
@Component
public class SpringMessagePublisher extends AbstractMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SpringMessagePublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        super(objectMapper);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    protected PublishResult doPublish(String topic, String key, String jsonMessage, String messageId) {
        try {
            SendResult<String, String> result;
            if (key != null) {
                result = kafkaTemplate.send(topic, key, jsonMessage).get(5, TimeUnit.SECONDS);
            } else {
                result = kafkaTemplate.send(topic, jsonMessage).get(5, TimeUnit.SECONDS);
            }
            
            return new PublishResult(
                    messageId,
                    result.getRecordMetadata().topic(),
                    key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    Instant.now()
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("메시지 발행 실패 - topic: {}", topic, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}

