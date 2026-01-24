package com.example.nats.component.jetstream;

import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API로 전송하는 메시지는 JetStream 사용 (유실되면 안됨)
 */
@Slf4j
@Component
public class JetStreamComponent {

    private final JetStream jetStream;
    private final Dispatcher dispatcher;
    private final JetStreamMessageHandler messageHandler; // Inject handler
    private final Map<String, JetStreamSubscription> subscriptionMap = new ConcurrentHashMap<>();

    public JetStreamComponent(JetStream jetStream, Connection connection, JetStreamMessageHandler messageHandler) {
        this.jetStream = jetStream;
        this.dispatcher = connection.createDispatcher();
        this.messageHandler = messageHandler;

        // DLQ 모니터링: 최대 전송 횟수 초과 이벤트 구독
        Dispatcher advisoryDispatcher = connection.createDispatcher(msg -> {
            log.error("Advisory: Max deliveries exceeded for messsage: {}", new String(msg.getData()));
        });
        advisoryDispatcher.subscribe("$JS.EVENT.ADVISORY.CONSUMER.MAX_DELIVERIES.>");
    }

    public void publish(String subject, String message) throws IOException, JetStreamApiException {
        jetStream.publish(subject, message.getBytes());
        log.debug("Published message to JetStream - subject: {}, message: {}", subject, message);
    }

    // 일반 구독 (Auto Ack 여부 설정 가능)
    public void subscribe(String subject, boolean autoAck) throws IOException, JetStreamApiException {
        if (subscriptionMap.containsKey(subject)) {
            log.info("Already subscribed to subject: {}", subject);
            return;
        }

        PushSubscribeOptions options = PushSubscribeOptions.builder().build();

        // Use messageHandler directly
        JetStreamSubscription subscription = jetStream.subscribe(subject, dispatcher, messageHandler, autoAck, options);

        subscriptionMap.put(subject, subscription);
        log.info("Subscribed to JetStream subject: {}, autoAck: {}", subject, autoAck);
    }

    // DLQ 처리용 구독 (Auto Ack 여부 설정 가능 + DLQ 로직)
    public void subscribeDlqProcessor(String subject, boolean autoAck) throws IOException, JetStreamApiException {
        if (subscriptionMap.containsKey(subject)) {
            return;
        }

        // Ephemeral Consumer (임시 컨슈머) 생성: 구독 취소 시 컨슈머도 자동 삭제됨
        ConsumerConfiguration cc = ConsumerConfiguration.builder()
                .ackWait(Duration.ofSeconds(3)) // 30초는 너무 길어서 3초로 설정 (빠른 재전송)
                .maxDeliver(3) // 최대 3번 재전송 후 중단
                .build();

        PushSubscribeOptions options = PushSubscribeOptions.builder()
                .configuration(cc)
                .build();

        JetStreamSubscription subscription = jetStream.subscribe(subject, dispatcher, messageHandler, autoAck, options);

        subscriptionMap.put(subject, subscription);
        log.info("Subscribed to DLQ Processor subject: {}, autoAck: {}", subject, autoAck);
    }

    public void subscribeToDlqTopic(String subject, boolean autoAck) throws IOException, JetStreamApiException {
        if (subscriptionMap.containsKey(subject)) {
            return;
        }

        PushSubscribeOptions options = PushSubscribeOptions.builder().build();

        // Use messageHandler directly for DLQ topic as well
        JetStreamSubscription subscription = jetStream.subscribe(subject, dispatcher, messageHandler, autoAck, options);

        subscriptionMap.put(subject, subscription);
        log.info("Subscribed to DLQ Topic subject: {}, autoAck: {}", subject, autoAck);
    }

    public void unsubscribe(String subject) {
        JetStreamSubscription subscription = subscriptionMap.remove(subject);
        if (subscription != null) {
            subscription.unsubscribe();
            log.info("Unsubscribed from JetStream subject: {}", subject);
        } else {
            log.warn("Subscription not found for subject: {}", subject);
        }
    }
}
