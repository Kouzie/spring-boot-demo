package com.example.nats.config;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.impl.ErrorListenerLoggerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.Duration;

/**
 * JetStream 전용 설정
 * Connection Bean은 common-config의 NatsConnectionConfig에서 제공
 * 
 * @SpringBootApplication의 scanBasePackages로 스캔됨
 */
@Slf4j
@Configuration
public class NatsConfig {

    @Value("${nats.core.uri}")
    private String uri;

    @Value("${nats.core.username}")
    private String username;

    @Value("${nats.core.password}")
    private String password;

    @Bean
    Connection initConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(uri)
                .userInfo(username, password)
                .errorListener(new ErrorListenerLoggerImpl())
                .connectionListener((conn, type) -> log.info("connection, type:{}", type.toString()))
                .reconnectDelayHandler(totalTries -> {
                    log.info("reconnection tries:{}", totalTries);
                    return Duration.ofMillis(1000);
                })
                .build();
        return Nats.connect(options);
    }

    @Bean
    JetStream jetStream(Connection connection) throws IOException, JetStreamApiException {
        JetStream js = connection.jetStream();

        // 기본 Stream 생성 (없으면 자동 생성)
        try {
            StreamConfiguration streamConfig = StreamConfiguration.builder()
                    .name("default-stream")
                    .subjects("jetstream.nats.>", "test.>", "default.>")
                    .build();
            connection.jetStreamManagement().addStream(streamConfig);
            log.info("JetStream default-stream created or already exists");
        } catch (JetStreamApiException e) {
            if (e.getErrorCode() == 10058) { // Stream already exists
                log.debug("Stream already exists");
            } else {
                log.warn("Failed to create stream: {}", e.getMessage());
            }
        }

        return js;
    }
}
