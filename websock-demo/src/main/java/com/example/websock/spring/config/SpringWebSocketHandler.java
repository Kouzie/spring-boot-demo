package com.example.websock.spring.config;

import com.example.websock.TestComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Profile("spring")
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringWebSocketHandler extends TextWebSocketHandler {

    private static Set<WebSocketSession> sessions = new ConcurrentHashMap().newKeySet();
    private final TestComponent testComponent;

    @PostConstruct
    private void init() {
        new Thread(() -> {
            while (true) {
                for (WebSocketSession session : sessions) {
                    try {
                        Thread.sleep(1000);
                        WebSocketMessage message = new TextMessage(testComponent.currentTimeMillis().toString());
                        session.sendMessage(message);
                    } catch (Exception e) {
                        log.error("send message failed, type:{}, msg:{}", e.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        }).start();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);
        testComponent.currentTimeMillis();
        log.info("client{} connect", session.getRemoteAddress());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("client{} handle message:{}", session.getRemoteAddress(), message.getPayload());
        for (WebSocketSession webSocketSession : sessions) {
            if (session == webSocketSession) continue;
            webSocketSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
        log.info("client{} connect", session.getRemoteAddress());
    }


}
