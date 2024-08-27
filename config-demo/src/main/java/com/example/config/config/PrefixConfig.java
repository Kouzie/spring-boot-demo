package com.example.config.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
// getter, setter 정의 필수
@Getter
@Setter
public class PrefixConfig {
    private String name;
    private String version;
    private String description;

    public void printConfig() {
        System.out.println("PrefixConfig App Name: " + name);
        System.out.println("PrefixConfig App Version: " + version);
        System.out.println("PrefixConfig App Description: " + description);
    }

}