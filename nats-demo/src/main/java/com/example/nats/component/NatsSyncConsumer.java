package com.example.nats.component;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import io.nats.client.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@EnableAsync
@Slf4j
@Profile("sync")
@Component
@RequiredArgsConstructor
public class NatsSyncConsumer {

    private final Connection natsConnection;
    private final MessageHandler messageHandler;

    private Subscription subscription;

    @Value("${nats.stream.topic}")
    private String topic;
    @Value("${nats.stream.queue}")
    private String queue;

    private MessageHandlerThread thread;

    @PostConstruct
    private void init() {
        if (StringUtils.hasText(queue)) {
            subscription = natsConnection.subscribe(topic, queue);
        } else {
            subscription = natsConnection.subscribe(topic);
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
