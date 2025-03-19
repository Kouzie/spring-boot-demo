package com.example.config.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// export ENV_VALUE=helloworld
// export ENV_ID=kouzie
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemEnvConfig {

    @Value("#{systemEnvironment['HOME']}")  // 시스템 환경변수
    private String home;

    private String envValue = System.getenv("ENV_VALUE");

    @Value("${ENV_ID:null}")
    private String envId;

    @PostConstruct
    public void printConfig() {
        System.getenv().forEach((key, value) ->
                log.info("SystemEnvConfig <" + key + ": " + value + ">"));
        log.info("SystemEnvConfig Home: " + home);
        log.info("SystemEnvConfig envValue: " + envValue);
        log.info("SystemEnvConfig envId: " + envId);

    }
}
