package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnExpression("${feature.enable:true}") // application.properties 값으로 활성화 여부 결정
public class ConditionalConfig {
    @PostConstruct
    public void printConfig() {
        log.info("ConditionalConfig is enabled");
    }
}