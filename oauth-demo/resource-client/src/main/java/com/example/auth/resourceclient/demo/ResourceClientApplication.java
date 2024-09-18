package com.example.auth.resourceclient.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;

@SpringBootApplication
public class ResourceClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceClientApplication.class, args);
	}
}