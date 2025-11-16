package com.example.kafka.producer.dto;

/**
 * 메시지 발행 요청 DTO
 * SampleMessageDto 의존성 없이 직접 필드 정의
 */
public record PublishMessageRequest(
        String topic,
        String key,        // 메시지 키 (선택적, null 가능)
        String message,
        String sender,
        Integer priority
) {
}

