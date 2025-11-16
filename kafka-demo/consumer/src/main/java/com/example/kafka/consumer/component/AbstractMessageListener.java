package com.example.kafka.consumer.component;

import com.example.kafka.common.dto.SampleMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * 메시지 수신 템플릿 클래스
 * 공통 로직(역직렬화, 처리 위임)을 제공하고, 수신 로직만 하위 클래스에서 구현
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageListener {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    protected final ObjectMapper objectMapper;

    /**
     * 메시지 처리 (템플릿 메서드)
     *
     * @param rawMessage 원본 메시지 문자열
     * @param metadata   메시지 메타데이터 (key, topic, partition, offset 등)
     */
    protected void processMessage(String rawMessage, MessageMetadata metadata) {
        try {
            SampleMessageDto messageDto = objectMapper.readValue(rawMessage, SampleMessageDto.class);
            log.info("메시지 수신 - key: {}, topic: {}, partition: {}, offset: {}",
                    metadata.key(), metadata.topic(), metadata.partition(), metadata.offset());
            handle(messageDto);
        } catch (Exception e) {
            log.error("메시지 처리 실패 - value: {}", rawMessage, e);
        }
    }

    /**
     * 메시지 처리
     *
     * @param messageDto 처리할 메시지 DTO
     */
    public void handle(SampleMessageDto messageDto) {
        log.info("메시지 처리 ({} profile) - id: {}, message: {}, timestamp: {}",
                activeProfile, messageDto.id(), messageDto.message(), messageDto.timestamp());
    }

}

