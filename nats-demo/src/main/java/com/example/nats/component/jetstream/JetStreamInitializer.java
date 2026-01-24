package com.example.nats.component.jetstream;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JetStreamInitializer {

    private final JetStreamComponent jetStreamComponent;

    @PostConstruct
    public void init() throws Exception {
        log.info("Initializing JetStream subscriptions...");
        jetStreamComponent.subscribe("jetstream.nats.>", true);
        jetStreamComponent.subscribeDlqProcessor("jetstream.nats.test.fail", false);
        jetStreamComponent.subscribeToDlqTopic("jetstream.nats.dlq", true);
    }

    @PreDestroy
    public void destroy() {
        jetStreamComponent.unsubscribe("jetstream.nats.test");
        jetStreamComponent.unsubscribe("jetstream.nats.dlq");
        jetStreamComponent.unsubscribe("jetstream.nats.test.fail");
    }
}
