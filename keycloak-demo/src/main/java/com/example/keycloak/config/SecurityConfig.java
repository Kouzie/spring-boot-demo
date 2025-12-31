package com.example.keycloak.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/css/**", "/js/**", "/error", "/", "/select-realm")
                                                .permitAll()
                                                .requestMatchers("/manager/**").hasRole("manager")
                                                .requestMatchers("/user/**").hasAnyRole("manager", "user")
                                                .requestMatchers("/posts/**").hasAnyRole("manager", "user")
                                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf.disable())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/select-realm")
                                                .defaultSuccessUrl("/profile", true)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userAuthoritiesMapper(userAuthoritiesMapper())))
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"));
                return http.build();
        }

        @Bean
        public GrantedAuthoritiesMapper userAuthoritiesMapper() {
                return (authorities) -> {
                        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

                        authorities.forEach(authority -> {
                                if (authority instanceof OidcUserAuthority) {
                                        OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                                        Map<String, Object> userInfo = oidcUserAuthority.getUserInfo().getClaims();

                                        if (userInfo.containsKey("realm_access")) {
                                                Map<String, Object> realmAccess = (Map<String, Object>) userInfo
                                                                .get("realm_access");
                                                if (realmAccess.containsKey("roles")) {
                                                        ((List<String>) realmAccess.get("roles")).forEach(role -> {
                                                                mappedAuthorities.add(new SimpleGrantedAuthority(
                                                                                "ROLE_" + role));
                                                        });
                                                }
                                        }
                                }
                                mappedAuthorities.add(authority);
                        });

                        return mappedAuthorities;
                };
        }
}
