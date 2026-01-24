package com.example.nats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NatsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NatsDemoApplication.class, args);
	}

}
