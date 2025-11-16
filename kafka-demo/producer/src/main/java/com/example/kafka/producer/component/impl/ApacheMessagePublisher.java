package com.example.kafka.producer.component.impl;

import com.example.kafka.producer.component.AbstractMessagePublisher;
import com.example.kafka.producer.dto.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

/**
 * Apache Kafka 프로필용 메시지 발행자
 * 템플릿 메서드 패턴을 사용하여 발행 로직만 구현
 */
@Profile("apache")
@Slf4j
@Component
public class ApacheMessagePublisher extends AbstractMessagePublisher {

    private final KafkaProducer<String, String> kafkaProducer;

    public ApacheMessagePublisher(
            @Qualifier("kafkaProducer") KafkaProducer<String, String> kafkaProducer,
            ObjectMapper objectMapper) {
        super(objectMapper);
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    protected PublishResult doPublish(String topic, String key, String jsonMessage, String messageId) {
        ProducerRecord<String, String> record;
        if (key != null) {
            record = new ProducerRecord<>(topic, key, jsonMessage);
        } else {
            record = new ProducerRecord<>(topic, jsonMessage);
        }
        
        try {
            // send()는 Future를 반환하지만, 즉시 get()으로 동기 처리
            RecordMetadata metadata = kafkaProducer.send(record, new ProducerCallback()).get();
            
            return new PublishResult(
                    messageId,
                    metadata.topic(),
                    key,
                    metadata.partition(),
                    metadata.offset(),
                    Instant.now()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("메시지 발행 중단 - topic: {}, key: {}", topic, key, e);
            throw new RuntimeException("메시지 발행 실패: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("메시지 발행 실패 - topic: {}, key: {}", topic, key, e);
            throw new RuntimeException("메시지 발행 실패: " + e.getMessage(), e);
        }
    }

    @Slf4j
    public static class ProducerCallback implements org.apache.kafka.clients.producer.Callback {

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                log.error("메시지 발행 실패", exception);
            } else {
                log.debug("메시지 발행 성공 - topic: {}, partition: {}, offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        }
    }
}

