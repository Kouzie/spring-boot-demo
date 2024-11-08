package com.example.statemachine.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.example.statemachine.demo.model")
@SpringBootApplication
public class StateMachineDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(StateMachineDemoApplication.class, args);
    }
}