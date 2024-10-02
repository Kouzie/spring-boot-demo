package com.example.oauth2client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class ResourceClientConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(ClientRegistrationRepository clientRegistrationRepository,
                                                   HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2.clientRegistrationRepository(clientRegistrationRepository));
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation("http://localhost:9090");
        ClientRegistration clientRegistration = builder
                .clientId("oauth-client-demo")
                .clientSecret("secret")
                .clientName("Resource Client Demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oauth-client-demo")
                .scope(OidcScopes.OPENID, OidcScopes.PROFILE)
                .build();
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}