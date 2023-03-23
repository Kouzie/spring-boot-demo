package com.example.amqp.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("spring-amqp")
@Component
public class SpringAmqpConsumer {

    // bean factory 에서 queue 이름의 빈객체를 찾아 등록
    @RabbitListener(queues = "#{demoQueue.name}")
    public void receive(byte[] payload) throws InterruptedException {
        log.info("message:" + new String(payload));
    }
}