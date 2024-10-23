package com.example.auth.resourceclient.demo.config.jwt;

import com.example.auth.resourceclient.demo.client.KakaoOidc2UserService;
import com.example.auth.resourceclient.demo.client.NaverOAuth2UserService;
import com.example.auth.resourceclient.demo.client.SpringOidc2UserService;
import com.example.auth.resourceclient.demo.model.ResourceClientUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

@Profile("jwt")
@Configuration
public class ResourceClientJwtConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(ClientRegistrationRepository clientRegistrationRepository,
                                                   OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                   ResourceClientUserService resourceClientUserService,
                                                   OAuthLoginSuccessHandler oAuthLoginSuccessHandler,
                                                   JWTUtil jwtUtil,
                                                   HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .authorizedClientService(oAuth2AuthorizedClientService) // jdbc authorizedClientService 사용하도록 변경
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(new DelegatingOAuth2UserService(List.of(
                                        new NaverOAuth2UserService(resourceClientUserService)
                                )))
                                .oidcUserService(new DelegatingOAuth2UserService(List.of(
                                        new KakaoOidc2UserService(resourceClientUserService),
                                        new SpringOidc2UserService()
                                )))
                        )
                        .successHandler(oAuthLoginSuccessHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        );
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        };
    }
}