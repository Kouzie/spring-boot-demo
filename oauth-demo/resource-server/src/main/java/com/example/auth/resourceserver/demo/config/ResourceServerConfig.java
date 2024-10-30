package com.example.auth.resourceserver.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    @Profile("oidc")
    public SecurityFilterChain jwtResourceServer(HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwtConfigurer -> jwtConfigurer.jwkSetUri("http://localhost:9090/oauth2/jwks"))
        );
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/userinfo").hasAuthority("SCOPE_profile") // 해당 권한이 있어야 /userinfo 접근 가능
                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }


    @Bean
    @Profile("opaque")
    public SecurityFilterChain opaqueResourceServer(HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(resourceServer -> resourceServer
                        .opaqueToken(configurer -> configurer
                                .introspectionUri("http://localhost:9090/oauth2/introspect")
                                .introspectionClientCredentials("oauth-demo-client-id", "secret")
                        )
        );
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/userinfo").hasAuthority("SCOPE_profile") // 해당 권한이 있어야 /userinfo 접근 가능
                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}