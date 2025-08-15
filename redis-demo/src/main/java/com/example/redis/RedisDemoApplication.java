package com.example.redis;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;


@SpringBootApplication
public class RedisDemoApplication {
    public static Lorem lorem = LoremIpsum.getInstance();
    public static Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }
}
