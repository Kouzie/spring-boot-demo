package com.example.nats.component.core;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * subscription.nextMessage 를 통해 주기적으로 메세지를 가져오는 방법
 * 잘 사용하지 않고 대부분 Dispatcher 방식을 사용함
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("sync")
public class CoreNatsSyncConsumer {

    private final Connection natsConnection;
    private final CoreNatsCustomMessageHandler messageHandler;
    private Subscription subscription;
    private MessageHandlerThread thread;

    @PostConstruct
    private void init() {
        String subject = "core.nats.subject";
        String queue = "default.queue";
        if (StringUtils.hasText(queue)) {
            subscription = natsConnection.subscribe(subject, queue);
        } else {
            subscription = natsConnection.subscribe(subject);
        }
        this.thread = new MessageHandlerThread();
        thread.start();
    }

    @PreDestroy
    private void destroy() {
        log.info("destroy");
        subscription.unsubscribe();
        thread.interrupt();
    }

    public class MessageHandlerThread extends Thread {

        public void run() {
            System.out.println(this.getName() + ": New Thread is running...");
            while (true) {
                try {
                    Message message = subscription.nextMessage(10000);
                    if (message != null)
                        messageHandler.onMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
