package com.example.config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:extra.properties")
@RequiredArgsConstructor
public class ExtraConfig {

    @Value("${test.name}")
    private String appName;

    @Value("${test.version}")
    private String appVersion;

    private final Environment env;

    public void printConfig() {
        System.out.println("ExtraConfig App Name: " + appName);
        System.out.println("ExtraConfig App Version: " + appVersion);
        System.out.println("ExtraConfig Env App Name: " + env.getProperty("test.name"));
        System.out.println("ExtraConfig Env App Version: " + env.getProperty("test.version"));
    }
}