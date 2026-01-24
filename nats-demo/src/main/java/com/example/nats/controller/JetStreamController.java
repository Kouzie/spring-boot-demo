package com.example.nats.controller;

import com.example.nats.component.jetstream.JetStreamComponent;
import com.example.nats.dto.JetStreamMessageDto;
import com.example.nats.dto.NatsSubscribeDto;
import io.nats.client.JetStreamApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jetstream")
public class JetStreamController {

    private final JetStreamComponent jetStreamComponent;

    @PostMapping("/publish")
    public void publish(@RequestBody JetStreamMessageDto dto) throws IOException, JetStreamApiException {
        jetStreamComponent.publish(dto.getSubject(), dto.getMessage());
        log.info("Published message using JetStream - subject: {}", dto.getSubject());
    }

    @PostMapping("/publish/dlq")
    public void publishDlqTest(@RequestBody JetStreamMessageDto dto) throws IOException, JetStreamApiException {
        jetStreamComponent.publish("jetstream.nats.test.fail", dto.getMessage());
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody NatsSubscribeDto dto) throws IOException, JetStreamApiException {
        jetStreamComponent.subscribe(dto.getSubject(), dto.isAutoAck());
    }

    @DeleteMapping("/unsubscribe")
    public void unsubscribe(@RequestBody NatsSubscribeDto dto) {
        jetStreamComponent.unsubscribe(dto.getSubject());
    }

}
