package com.example.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AwsDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AwsDemoApplication.class, args);
    }
}
