package com.example.websock.tomcat.config;

import com.example.websock.TestComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TomcatSocket 은 싱글톤 객체가 아니며 socket 이 연결될때 마다 인스턴스가 새로 생성된다.
 * 하지만 socket 등록과정에서(@ServerEndpoint) 해당 객체는 최소 한번 spring bean 으로 등록되어야 한다.
 * 싱글톤이 아니기에 autowired 와 같은 spring bean 등록이 불가능하여 SpringContext 와 같은 코드를 사용해야함
 * */
@Profile("tomcat")
@Slf4j
@Component
@ServerEndpoint(value = "/websocket")
public class TomcatWebSocketSocket {
    private Session session;
    public static List<TomcatWebSocketSocket> listeners = new ArrayList<>();
    private static int onlineCount = 0;
    private TestComponent testComponent;

    @PostConstruct
    private void init() {
        this.testComponent = SpringContext.getBean(TestComponent.class);
        new Thread(() -> {
            while (true) {
                for (TomcatWebSocketSocket session : listeners) {
                    try {
                        Thread.sleep(1000);
                        session.sendMessage(testComponent.currentTimeMillis().toString());
                    } catch (Exception e) {
                        log.error("send message failed, type:{}, msg:{}", e.getClass().getSimpleName(), e.getMessage());
                    }
                }
            }
        }).start();
    }

    @OnOpen //클라이언트가 소켓에 연결되때 마다 호출
    public void onOpen(Session session) {
        onlineCount++;
        log.info("onOpen called, userCount:" + onlineCount);
        this.session = session;
        listeners.add(this);
    }

    @OnClose //클라이언트와 소켓과의 연결이 닫힐때 (끊길떄) 마다 호
    public void onClose(Session session) {
        onlineCount--;
        log.info("onClose called, userCount:" + onlineCount);
        listeners.remove(this);
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("onMessage called, message:" + message);
        broadcast(message);
    }

    @OnError //의도치 않은 에러 발생
    public void onError(Session session, Throwable throwable) {
        log.warn("onClose called, error:" + throwable.getMessage());
        listeners.remove(this);
    }

    public void broadcast(String message) {
        for (TomcatWebSocketSocket listener : listeners) {
            if (listener == this) continue;
            listener.sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.warn("Caught exception while sending message to Session " + this.session.getId() + "error:" + e.getMessage());
        }
    }
}
