package com.example.amqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AmqpApplication {

    public static final String[] topics = {
            ".computer.part.cpu",
            ".computer.part.monitor",
            ".computer.part.keyboard",
            ".computer.part.gpu",
            ".computer.part.ram"};

    public static void main(String[] args) {
        SpringApplication.run(AmqpApplication.class, args);
    }
}
