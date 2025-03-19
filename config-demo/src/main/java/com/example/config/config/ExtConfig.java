package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@PropertySource("classpath:extra.properties")
@RequiredArgsConstructor
public class ExtConfig {

    @Value("${ext.test.value}")
    private String extTestValue;

    @PostConstruct
    public void printConfig() {
        log.info("ExtConfig Ext Test Value: " + extTestValue);
    }
}