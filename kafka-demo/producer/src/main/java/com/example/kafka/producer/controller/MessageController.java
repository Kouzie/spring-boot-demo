package com.example.kafka.producer.controller;

import com.example.kafka.common.dto.SampleMessageDto;
import com.example.kafka.producer.component.AbstractMessagePublisher;
import com.example.kafka.producer.dto.PublishMessageRequest;
import com.example.kafka.producer.dto.PublishResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final AbstractMessagePublisher messagePublisher;

    /**
     * 메시지 발행
     * key는 request body에 포함 (선택적)
     * 
     * @param request 발행 요청 (topic, key, message 포함)
     * @return 발행 결과 메시지
     */
    @PostMapping
    public String sendMessage(@RequestBody PublishMessageRequest request) {
        String messageId = UUID.randomUUID().toString();
        // id와 timestamp를 컨트롤러에서 생성하여 SampleMessageDto로 변환
        SampleMessageDto messageDto = new SampleMessageDto(
                messageId,
                request.message(),
                request.sender(),
                Instant.now(),
                request.priority()
        );
        
        PublishResult result = messagePublisher.publish(request.topic(), request.key(), messageDto);
        
        // 상세 로그 출력
        log.info("메시지 발행 완료 - messageId: {}, topic: {}, key: {}, partition: {}, offset: {}, publishedAt: {}", 
                result.messageId(), result.topic(), result.key(), result.partition(), result.offset(), result.publishedAt());
        
        if (request.key() != null) {
            return String.format("메시지가 발행되었습니다 (key: %s) - messageId: %s, partition: %d, offset: %d", 
                    request.key(), result.messageId(), result.partition(), result.offset());
        } else {
            return String.format("메시지가 발행되었습니다 - messageId: %s, partition: %d, offset: %d", 
                    result.messageId(), result.partition(), result.offset());
        }
    }
}

