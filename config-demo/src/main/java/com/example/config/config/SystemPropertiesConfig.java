package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

// java -jar -Dmy.property=myProperty build/libs/config-demo-0.0.1-SNAPSHOT.jar
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemPropertiesConfig {

    @Value("#{systemProperties['user.name']}")  // 시스템 속성
    private String systemUser;

    public String getAppName() {
        return appName;
    }

    @Value("${app.name}") // application.properties 값 가져오기
    private String appName;

    @Value("${app.version}") // application.properties 값 가져오기
    private String appVersion;

    @PostConstruct
    public void printConfig() {
        Properties properties = System.getProperties();
        properties.forEach((key, value) -> log.info("SystemPropertiesConfig <{}: {}>", key, value));
        log.info("SystemPropertiesConfig My Property: {}", System.getProperty("my.property"));
        log.info("systemUser: {}", systemUser);
        log.info("appName: {}", appName);
        log.info("appVersion: {}", appVersion);
    }
}
