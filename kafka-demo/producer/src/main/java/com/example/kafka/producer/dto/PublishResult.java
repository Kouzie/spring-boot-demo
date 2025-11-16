package com.example.kafka.producer.dto;

import java.time.Instant;

/**
 * 메시지 발행 결과 DTO
 */
public record PublishResult(
        String messageId,
        String topic,
        String key,
        Integer partition,
        Long offset,
        Instant publishedAt
) {
}

