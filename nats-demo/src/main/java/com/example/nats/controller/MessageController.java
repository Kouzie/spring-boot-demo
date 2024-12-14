package com.example.nats.controller;

import com.example.nats.component.NatsComponent;
import com.example.nats.dto.NatsMessageDto;
import com.example.nats.dto.NatsSubscribeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nats")
public class MessageController {

    private final NatsComponent natsComponent;

    @PostMapping("/publish")
    public void publish(@RequestBody NatsMessageDto dto) {
        if (StringUtils.hasText(dto.getReplyTo())) {
            natsComponent.publish(dto.getSubject(), dto.getMessage(), dto.getReplyTo());
        } else {
            natsComponent.publish(dto.getSubject(), dto.getMessage());
        }
    }


    @PostMapping("/subscribe")
    public void subscribe(@RequestBody NatsSubscribeDto dto) {
        natsComponent.subscribe(dto.getSubject());
    }

    @DeleteMapping("/unsubscribe")
    public void unsubscribe(@RequestBody NatsSubscribeDto dto) {
        natsComponent.unsubscribe(dto.getSubject());
    }
}
