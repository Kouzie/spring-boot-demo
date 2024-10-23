package com.example.auth.resourceclient.demo.config.session;

import com.example.auth.resourceclient.demo.client.KakaoOidc2UserService;
import com.example.auth.resourceclient.demo.client.NaverOAuth2UserService;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Profile("session")
@Configuration
public class ResourceClientSessionConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(ClientRegistrationRepository clientRegistrationRepository,
                                                   OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                   ResourceClientUserService resourceClientUserService,
                                                   HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .defaultSuccessUrl("/main", false) // alwaysUse 는 이전 방문 페이지로 이동시킴
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(new DelegatingOAuth2UserService(List.of(
                                        new NaverOAuth2UserService(resourceClientUserService),
                                        new DefaultOAuth2UserService()
                                )))
                                .oidcUserService(new DelegatingOAuth2UserService(List.of(
                                        new KakaoOidc2UserService(resourceClientUserService),
                                        new OidcUserService()
                                )))
                        )
                        .authorizedClientService(oAuth2AuthorizedClientService) // jdbc authorizedClientService 사용하도록 변경
                        .loginPage("/login")
                );
        return http.build();
    }

}
