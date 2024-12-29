package com.example.security.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class DefaultSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder(4);
        String salt = ""; // Empty string as salt
        return new BCryptPasswordEncoder(10, new SecureRandom(salt.getBytes()));
        // 생성시기에 따라 Salt 값이 달라지기에 별 의미 없음
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring()
                        .requestMatchers("/auth/login_demo")
                        .requestMatchers("/error")
                        .requestMatchers("/favicon.ico")
                        .requestMatchers("/h2-console/**");
            }
        };
    }

    // Role 계층 정의
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("""
                    ROLE_ADMIN > ROLE_MANAGER
                    ROLE_MANAGER > ROLE_USER
                """);
        return roleHierarchy;
    }
}