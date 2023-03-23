package com.example.amqp.normal;

import com.example.amqp.AmqpApplication;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Slf4j
@Profile("amqp")
@Component
@RequiredArgsConstructor
public class AmqpProducer {

    private final Channel channel;
    private final Random random = new Random();

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() throws IOException {
        String key = AmqpApplication.topics[random.nextInt(4)];
        String message = "Hello RabbitMQ, key:" + key;
        publishingMessage(message, key);
    }

    public void publishingMessage(String message, String routingKey) throws IOException {
        byte[] messageBodyBytes = message.getBytes();
        // null부분은 basicProperties 가 들어가는데 header 와 같은 역할이다.
        channel.basicPublish("amq.topic", routingKey, null, messageBodyBytes);
    }
}
