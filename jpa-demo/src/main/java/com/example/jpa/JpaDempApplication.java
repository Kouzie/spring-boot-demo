package com.example.jpa;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Random;

@EnableJpaAuditing
@SpringBootApplication
public class JpaDempApplication {
    public static Lorem lorem = LoremIpsum.getInstance();
    public static Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(JpaDempApplication.class, args);
    }
}
