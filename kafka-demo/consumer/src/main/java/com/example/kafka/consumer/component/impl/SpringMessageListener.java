package com.example.kafka.consumer.component.impl;

import com.example.kafka.consumer.component.AbstractMessageListener;
import com.example.kafka.consumer.component.MessageMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Spring Kafka 프로필용 메시지 수신 리스너
 * 템플릿 메서드 패턴을 사용하여 수신 로직만 구현
 */
@Profile("spring")
@Slf4j
@Component
public class SpringMessageListener extends AbstractMessageListener {

    public SpringMessageListener(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    // test1 토픽 리스너 (auto commit - RECORD 모드)
    @KafkaListener(
            topics = "test1",
            groupId = "test1-group"
    )
    public void consumeTest1Message(ConsumerRecord<String, String> record) {
        MessageMetadata metadata = new MessageMetadata(
                record.key(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.timestamp()
        );
        processMessage(record.value(), metadata);
        // auto commit이므로 수동 커밋 불필요
    }

    // test2 토픽 리스너 (수동 커밋 - MANUAL 모드)
    @KafkaListener(
            topics = "test2",
            groupId = "test2-group",
            containerFactory = "manualKafkaListenerContainerFactory"
    )
    public void consumeTest2Message(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            MessageMetadata metadata = new MessageMetadata(
                    record.key(),
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.timestamp()
            );
            processMessage(record.value(), metadata);

            // 메시지 처리 성공 후 수동 커밋
            acknowledgment.acknowledge();
            log.debug("Test2 Offset 커밋 완료 - topic: {}, partition: {}, offset: {}",
                    record.topic(), record.partition(), record.offset());
        } catch (Exception e) {
            log.error("Test2 메시지 처리 실패 - topic: {}, partition: {}, offset: {}",
                    record.topic(), record.partition(), record.offset(), e);
            // 에러 발생 시 커밋하지 않음 (재처리를 위해)
        }
    }
}

