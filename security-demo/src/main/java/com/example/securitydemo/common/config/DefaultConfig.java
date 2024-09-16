package com.example.securitydemo.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DefaultConfig  {

    @Bean
    public PasswordEncoder passwordEncoder () {
        //return new BCryptPasswordEncoder(4);
        String salt = ""; // Empty string as salt
        return new BCryptPasswordEncoder(10, new SecureRandom(salt.getBytes()));
        // 생성시기에 따라 Salt 값이 달라지기에 별 의미 없음
    }
}