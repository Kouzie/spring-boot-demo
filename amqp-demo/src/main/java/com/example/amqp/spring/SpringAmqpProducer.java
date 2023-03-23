package com.example.amqp.spring;

import com.example.amqp.AmqpApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("spring-amqp")
@Component
@RequiredArgsConstructor
public class SpringAmqpProducer {

    private final RabbitTemplate template;
    private final TopicExchange topic;
    private final Random random = new Random();

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        String key = AmqpApplication.topics[random.nextInt(4)];
        String message = "Hello RabbitMQ, key:" + key;
        publishingMessage(message, key);
    }

    public void publishingMessage(String message, String key) {
        template.convertAndSend(topic.getName(), key, message.getBytes());
    }
}
