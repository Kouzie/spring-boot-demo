package com.example.nats.component;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NatsComponent {

    private final Connection natsConnection;
    private final Dispatcher dispatcher;

    public NatsComponent(Connection connection, MessageHandler messageHandler) {
        this.natsConnection = connection;
        this.dispatcher = natsConnection.createDispatcher(messageHandler);
    }

    /**
     * NatsDispatcher 의 경우 Runnable 의 구현체로 이루어져 있으며
     * 내부에서 while 루프를 돌며 메세지를 꺼내와 messageHandler 로 메세지를 넘긴다
     */

    public void publish(String topic, String message) {
        natsConnection.publish(topic, message.getBytes());
    }

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
