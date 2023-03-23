package com.example.nats;

import com.example.nats.component.NatsPublisher;
import com.example.nats.dto.TopicMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final NatsPublisher natsPublisher;

    @PostMapping
    public String sendMessage(@RequestBody TopicMessageDto message) throws IOException {
        natsPublisher.publish(message);
        return "send success";
    }
}
