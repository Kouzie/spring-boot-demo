package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
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

    @PostConstruct
    public void printConfig() {
        log.info("FullNameConfig App Name: " + appName);
        log.info("FullNameConfig App Version: " + appVersion);
        log.info("FullNameConfig App Unknown: " + unknown);
        log.info("FullNameConfig App Numbers: " + String.join(",", numbers));
        log.info("FullNameConfig Env App Name: " + env.getProperty("app.name"));
        log.info("FullNameConfig Env App Version: " + env.getProperty("app.version"));
    }
}