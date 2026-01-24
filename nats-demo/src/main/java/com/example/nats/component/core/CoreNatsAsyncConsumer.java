package com.example.nats.component.core;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Dispatcher를 사용한 비동기 메시지 처리 방식
 * Dispatcher는 내부적으로 별도 스레드에서 메시지를 처리하므로 비동기적으로 동작함
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("async")
public class CoreNatsAsyncConsumer {

    private final Connection natsConnection;
    private final CoreNatsCustomMessageHandler messageHandler;
    private Dispatcher dispatcher;

    @PostConstruct
    private void init() {
        String subject = "core.nats.subject";
        String queue = "default.queue";

        dispatcher = natsConnection.createDispatcher(messageHandler);

        if (StringUtils.hasText(queue)) {
            dispatcher.subscribe(subject, queue);
        } else {
            dispatcher.subscribe(subject);
        }

        log.info("NatsAsyncConsumer initialized with subject: {}", subject);
    }

    @PreDestroy
    private void destroy() {
        log.info("destroy NatsAsyncConsumer");
        if (dispatcher != null) {
            dispatcher.unsubscribe("core.nats.subject");
        }
    }
}
