package com.example.nats.component.core;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreNatsCustomMessageHandler implements MessageHandler {

    private final Connection natsConnection;

    @Override
    public void onMessage(Message msg) {
        log.info("subject:{}, message:{}", msg.getSubject(), new String(msg.getData()));
        if (StringUtils.hasText(msg.getReplyTo())) {
            natsConnection.publish(msg.getReplyTo(), "OK RECEIVED".getBytes());
        }
    }
}
