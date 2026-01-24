package com.example.nats.component.core;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoreNatsComponent {

    private final Connection natsConnection;
    private final Dispatcher dispatcher;

    @Autowired(required = false)
    public CoreNatsComponent(Connection connection, CoreNatsCustomMessageHandler messageHandler) {
        this.natsConnection = connection;
        if (messageHandler != null) {
            this.dispatcher = natsConnection.createDispatcher(messageHandler);
        } else {
            this.dispatcher = natsConnection.createDispatcher();
        }
    }

    /**
     * NatsDispatcher 의 경우 Runnable 의 구현체로 이루어져 있으며
     * 내부에서 while 루프를 돌며 메세지를 꺼내와 messageHandler 로 메세지를 넘긴다
     */

    public void publish(String topic, String message) {
        natsConnection.publish(topic, message.getBytes());
    }

    // replyTo 는 core nats 에만 있는 개념
    public void publish(String subject, String message, String replyTo) {
        natsConnection.publish(subject, replyTo, message.getBytes());
    }

    public void subscribe(String subject) {
        dispatcher.subscribe(subject); // 내부에서 concurrent hashmap 으로 관리함
    }

    public void unsubscribe(String subject) {
        dispatcher.unsubscribe(subject);
    }
}
