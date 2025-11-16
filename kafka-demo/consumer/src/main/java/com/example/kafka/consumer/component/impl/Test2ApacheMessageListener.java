package com.example.kafka.consumer.component.impl;

import com.example.kafka.consumer.component.AbstractMessageListener;
import com.example.kafka.consumer.component.MessageMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Apache Kafka 프로필용 test2 토픽 메시지 수신 리스너
 * 수동 커밋 사용
 * SmartLifecycle을 구현하여 애플리케이션 종료 시 우아하게 종료되도록 관리
 */
@Profile("apache")
@Slf4j
@Component
public class Test2ApacheMessageListener extends AbstractMessageListener implements SmartLifecycle {

    private final KafkaConsumer<String, String> consumer;
    private Thread consumerThread;
    private volatile boolean running = false;

    public Test2ApacheMessageListener(
            @Qualifier("test2KafkaConsumer") KafkaConsumer<String, String> consumer,
            ObjectMapper objectMapper) {
        super(objectMapper);
        this.consumer = consumer;
        log.info("Test2ApacheMessageListener 빈 생성 완료 (스레드 시작 전)");
    }

    @Override
    public void start() {
        log.info("Test2ApacheMessageListener 시작 (SmartLifecycle.start)");
        this.running = true;
        this.consumerThread = new Thread(() -> {
            log.info("Test2 Consumer 스레드 시작 (수동 커밋)");
            try {
                while (running) {
                    try {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                        if (!records.isEmpty()) {
                            log.info("Test2 메시지 {}개 수신", records.count());
                        }
                        for (ConsumerRecord<String, String> record : records) {
                            MessageMetadata metadata = new MessageMetadata(
                                    record.key(),
                                    record.topic(),
                                    record.partition(),
                                    record.offset(),
                                    null
                            );
                            processMessage(record.value(), metadata);
                        }
                        // 수동 커밋
                        if (!records.isEmpty()) {
                            consumer.commitAsync();
                        }
                    } catch (WakeupException e) {
                        log.info("Test2 Consumer 스레드 wakeup 수신, 종료 프로세스 시작");
                    } catch (Exception e) {
                        log.error("Test2 Consumer poll 오류", e);
                    }
                }
            } finally {
                try {
                    // 마지막으로 동기 커밋 시도
                    consumer.commitSync();
                } finally {
                    consumer.close();
                    log.info("Test2 Consumer 스레드 종료 및 리소스 정리 완료");
                }
            }
        });
        this.consumerThread.start();
    }

    @Override
    public void stop() {
        log.info("Test2ApacheMessageListener 중지 (SmartLifecycle.stop)");
        this.running = false;
        consumer.wakeup(); // poll() 블록을 즉시 해제
        try {
            if (consumerThread != null) {
                consumerThread.join(5000); // 스레드가 종료될 때까지 최대 5초 대기
            }
        } catch (InterruptedException e) {
            log.error("Test2 Consumer 스레드 join 중 오류 발생", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean isAutoStartup() {
        return true; // 애플리케이션 시작 시 자동으로 start() 호출
    }
}

