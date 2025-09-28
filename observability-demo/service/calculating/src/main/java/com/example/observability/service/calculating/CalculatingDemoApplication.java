package com.example.observability.service.calculating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.observability")
public class CalculatingDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatingDemoApplication.class, args);
    }
}
