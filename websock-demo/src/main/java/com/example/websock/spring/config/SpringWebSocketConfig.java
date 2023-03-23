package com.example.websock.spring.config;

import com.example.websock.TestComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Profile("spring")
@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SpringWebSocketConfig implements WebSocketConfigurer {

    private final SpringWebSocketHandler springWebSocketHandler;
    private final TestComponent testComponent;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(springWebSocketHandler, "/websocket")
                .setAllowedOrigins("*")
                .withSockJS(); // sockjs 지원
        registry.addHandler(springWebSocketHandler, "/websocket")
                .setAllowedOrigins("*");
    }
}