package com.example.nats.component;

import com.example.nats.component.core.CoreNatsComponent;
import com.example.nats.component.jetstream.JetStreamComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsScheduler {

    private final CoreNatsComponent natsComponent;
    private final JetStreamComponent jetStreamComponent;

    private static final String CORE_SUBJECT = "core.nats.subject";
    private static final String JETSTREAM_SUBJECT = "jetstream.nats.test";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void sendScheduledMessage() {
        try {
            String currentTime = LocalDateTime.now().format(FORMATTER);
            String randomString = UUID.randomUUID().toString().substring(0, 8);
            String message = String.format("%s - %s", currentTime, randomString);

            // Send to Core NATS
            natsComponent.publish(CORE_SUBJECT, message);
            log.debug("Sent to Core NATS: {}", message);

            // Send to JetStream
            jetStreamComponent.publish(JETSTREAM_SUBJECT, message);
            log.debug("Sent to JetStream: {}", message);

        } catch (Exception e) {
            log.error("Failed to send scheduled messages", e);
        }
    }
}
