package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app")
// getter, setter 정의 필수
@Getter
@Setter
public class PrefixConfig {
    private String name;
    private String version;
    private String description;

    @PostConstruct
    public void printConfig() {
        log.info("PrefixConfig App Name: " + name);
        log.info("PrefixConfig App Version: " + version);
        log.info("PrefixConfig App Description: " + description);
    }

}