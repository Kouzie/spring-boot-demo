package com.example.config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
@Configuration
public class FullNameConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.unknown:notexist}")
    private String unknown;

    @Value("${app.number.arr}")
    private String[] numbers;

    private final Environment env;

    public void printConfig() {
        System.out.println("FullNameConfig App Name: " + appName);
        System.out.println("FullNameConfig App Version: " + appVersion);
        System.out.println("FullNameConfig App Unknown: " + unknown);
        System.out.println("FullNameConfig App Numbers: " + String.join(",", numbers));
        System.out.println("FullNameConfig Env App Name: " + env.getProperty("app.name"));
        System.out.println("FullNameConfig Env App Version: " + env.getProperty("app.version"));
    }
}