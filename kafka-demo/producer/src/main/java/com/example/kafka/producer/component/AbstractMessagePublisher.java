package com.example.kafka.producer.component;

import com.example.kafka.common.dto.SampleMessageDto;
import com.example.kafka.producer.dto.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * 메시지 발행 템플릿 클래스
 * 공통 로직(직렬화, DTO 설정)을 제공하고, 발행 로직만 하위 클래스에서 구현
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher {

    protected final ObjectMapper objectMapper;

    /**
     * 메시지 발행 (템플릿 메서드)
     * key가 null이면 key 없이 발행, key가 있으면 key와 함께 발행
     *
     * @param topic 토픽명
     * @param key 메시지 키 (null 가능)
     * @param messageDto 발행할 메시지 DTO (id와 timestamp는 이미 설정된 상태)
     * @return 발행 결과
     */
    public PublishResult publish(String topic, String key, SampleMessageDto messageDto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(messageDto);
            
            // 하위 클래스에서 구현한 발행 로직 실행
            PublishResult result = doPublish(topic, key, jsonMessage, messageDto.id());
            
            if (key != null) {
                log.info("메시지 발행 완료 - topic: {}, key: {}, id: {}, partition: {}, offset: {}", 
                        topic, key, result.messageId(), result.partition(), result.offset());
            } else {
                log.info("메시지 발행 완료 - topic: {}, id: {}, partition: {}, offset: {}", 
                        topic, result.messageId(), result.partition(), result.offset());
            }
            return result;
        } catch (Exception e) {
            log.error("메시지 발행 실패 - topic: {}, key: {}", topic, key, e);
            throw new RuntimeException("메시지 발행 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 실제 메시지 발행 로직 (하위 클래스에서 구현)
     *
     * @param topic 토픽명
     * @param key 메시지 키 (null 가능)
     * @param jsonMessage JSON 문자열
     * @param messageId 메시지 ID
     * @return 발행 결과
     */
    protected abstract PublishResult doPublish(String topic, String key, String jsonMessage, String messageId);
}

