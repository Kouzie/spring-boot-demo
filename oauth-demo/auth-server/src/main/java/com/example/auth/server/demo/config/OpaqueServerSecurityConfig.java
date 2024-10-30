package com.example.auth.server.demo.config;

import com.example.auth.server.demo.user.AuthUserEntity;
import com.example.auth.server.demo.user.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Profile("opaque")
@Configuration
@RequiredArgsConstructor
public class OpaqueServerSecurityConfig {

    private final AuthUserService authUserService;

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            // db 조회하지 않고 context 를 변경하고 싶다면 JpaOAuth2AuthorizationService 에서 Authentication entity(DB) 에 관련내용이 포함된 context 를 저장해두어야함.
            AuthUserEntity authUser = authUserService.findByUname(context.getPrincipal().getName()).orElseThrow();
            String email = authUser.getEmail();
            context.getClaims().claims(claims -> claims.put("email", email));
        };
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> tokenGenerator(OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer) {
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                accessTokenGenerator, // access_token
                refreshTokenGenerator // refresh_token
        );
    }

    // oauth core module 등록
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(RegisteredClientRepository registeredClientRepository,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2AuthorizationConsentService authorizationConsentService,
                                                                      OAuth2TokenGenerator<OAuth2Token> tokenGenerator,
                                                                      HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authz = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);


        authz
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(authorizationService)
                .authorizationConsentService(authorizationConsentService)
                .tokenGenerator(tokenGenerator)
                .authorizationEndpoint(configurer -> configurer.consentPage("/oauth2/consent"))
        ;
        http
                .securityMatchers(matchers -> matchers.requestMatchers(antMatcher("/oauth2/**"), authz.getEndpointsMatcher()))
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
        ;
        http.addFilterAfter(new PrintResponseBodyFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}