package com.example.kafka.consumer.component;

/**
 * 메시지 메타데이터를 담는 내부 클래스
 */
public record MessageMetadata(
        String key,
        String topic,
        int partition,
        long offset,
        Long timestamp) {
}