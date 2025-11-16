package com.example.kafka.common.dto;

import java.time.Instant;

/**
 * 샘플 메시지 DTO
 */
public record SampleMessageDto(
        String id,
        String message,
        String sender,
        Instant timestamp,
        Integer priority
) {
}
