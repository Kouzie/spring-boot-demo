package com.example.config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:extra.properties")
@RequiredArgsConstructor
public class ExtConfig {

    @Value("${ext.test.value}")
    private String extTestValue;

    public void printConfig() {
        System.out.println("ExtConfig Ext Test Value: " + extTestValue);
    }
}