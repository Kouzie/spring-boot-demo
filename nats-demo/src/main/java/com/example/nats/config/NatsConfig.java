package com.example.nats.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.impl.ErrorListenerLoggerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class NatsConfig {

    @Value("${nats.stream.uri}")
    private String uri;

    @Bean
    Connection initConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(uri)
                .userInfo("admin", "password")
                .errorListener(new ErrorListenerLoggerImpl())
                .connectionListener((conn, type) -> log.info("connection, type:{}", type.toString()))
                .reconnectDelayHandler(totalTries -> {
                    log.info("reconnection tries:{}", totalTries);
                    return Duration.ofMillis(1000);
                })
                .build();
        return Nats.connect(options);
    }
}
