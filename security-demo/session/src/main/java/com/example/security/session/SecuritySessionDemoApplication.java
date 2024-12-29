package com.example.security.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.security")
public class SecuritySessionDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecuritySessionDemoApplication.class, args);
    }
}