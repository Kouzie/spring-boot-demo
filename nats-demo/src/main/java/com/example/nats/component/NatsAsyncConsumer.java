package com.example.nats.component;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Slf4j
@Profile("async")
@Component
@RequiredArgsConstructor
public class NatsAsyncConsumer {

    private final Connection natsConnection;
    private final MessageHandler messageHandler;

    @Value("${nats.stream.topic}")
    private String topic;
    @Value("${nats.stream.queue}")
    private String queue;

    @PostConstruct
    private void init() {
        // CountDownLatch latch = new CountDownLatch(1);
        Dispatcher dispatcher = natsConnection.createDispatcher(messageHandler);
        if (StringUtils.hasText(queue)) {
            dispatcher.subscribe(topic, queue);
        } else {
            dispatcher.subscribe(topic);
        }
    }
}
