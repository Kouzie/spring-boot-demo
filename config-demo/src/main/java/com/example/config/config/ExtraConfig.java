package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@PropertySource("classpath:extra.properties")
@RequiredArgsConstructor
public class ExtraConfig {

    @Value("${test.name}")
    private String appName;

    @Value("${test.version}")
    private String appVersion;

    private final Environment env;

    @PostConstruct
    public void printConfig() {
        log.info("ExtraConfig App Name: " + appName);
        log.info("ExtraConfig App Version: " + appVersion);
        log.info("ExtraConfig Env App Name: " + env.getProperty("test.name"));
        log.info("ExtraConfig Env App Version: " + env.getProperty("test.version"));
    }
}