package com.example.websock.stomp.config;

import com.example.websock.stomp.dto.ClientMessage;
import com.example.websock.stomp.dto.Greeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
public class StompWebsocketHandler {

    @MessageMapping("/message")
    @SendTo("/topic/greetings")
    public Greeting greeting(ClientMessage message) throws Exception {
        log.info("message received, message:{}", message.getMessage());
        Thread.sleep(1000); // simulated delay
        return Greeting.builder()
                .content(HtmlUtils.htmlEscape(message.getMessage()))
                .now(LocalDateTime.now())
                .build();
    }
}
