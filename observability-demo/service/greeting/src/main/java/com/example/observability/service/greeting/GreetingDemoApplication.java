package com.example.observability.service.greeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.observability")
public class GreetingDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreetingDemoApplication.class, args);
    }
}
