package com.example.config.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

// java -jar -Dmy.property=myProperty build/libs/config-demo-0.0.1-SNAPSHOT.jar
@Configuration
@RequiredArgsConstructor
public class SystemPropertiesConfig {

    public void printConfig() {
        // Properties properties = System.getProperties();
        // properties.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("SystemPropertiesConfig My Property: " + System.getProperty("my.property"));
    }
}
