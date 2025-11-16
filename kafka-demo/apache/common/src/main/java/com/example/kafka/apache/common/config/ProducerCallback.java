package com.example.kafka.apache.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class ProducerCallback implements org.apache.kafka.clients.producer.Callback {

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.error("메시지 발행 실패", exception);
        } else {
            log.info("메시지 발행 성공 - topic: {}, partition: {}, offset: {}", 
                    metadata.topic(), metadata.partition(), metadata.offset());
        }
    }
}

