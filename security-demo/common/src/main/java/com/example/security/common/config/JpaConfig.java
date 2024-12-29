package com.example.security.common.config;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Random;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.security.common.repository") // Repository 패키지 경로
@EntityScan(basePackages = "com.example.security.common.model") // Repository 패키지 경로
public class JpaConfig {

    public static Lorem lorem = LoremIpsum.getInstance();
    public static Random random = new Random();
}
