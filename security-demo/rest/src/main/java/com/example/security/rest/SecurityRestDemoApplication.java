package com.example.security.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.security")
public class SecurityRestDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityRestDemoApplication.class, args);
    }
}