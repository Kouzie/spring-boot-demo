package com.example.config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// java -jar 명령으로만 환경변수를 인식함
// export ENV_VALUE=helloworld
// export ENV_ID=kouzie
@Configuration
@RequiredArgsConstructor
public class EnvConfig {

    private String envValue = System.getenv("ENV_VALUE");

    @Value("${ENV_ID:null}")
    private String envId;

    public void printConfig() {
        System.out.println("EnvConfig Env value: " + envValue);
        System.out.println("EnvConfig Env id: " + envId);
    }
}
