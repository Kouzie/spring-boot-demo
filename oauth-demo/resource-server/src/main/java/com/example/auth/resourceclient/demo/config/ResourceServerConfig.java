package com.example.auth.resourceclient.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain resourceServer(HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwtConfigurer -> jwtConfigurer.jwkSetUri("http://localhost:9090/oauth2/jwks")));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/userinfo").hasAuthority("SCOPE_profile") // 해당 권한이 있어야 /userinfo 접근 가능
                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}