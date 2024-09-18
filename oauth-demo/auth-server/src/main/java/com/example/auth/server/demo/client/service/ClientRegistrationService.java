package com.example.auth.server.demo.client.service;

import com.example.auth.server.demo.client.model.JpaRegisteredClientRepository;
import com.example.auth.server.demo.client.dto.ClientRegistrationRequest;
import com.example.auth.server.demo.client.dto.ClientRegistrationResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService {
    private final JpaRegisteredClientRepository repository;

    @PostConstruct
    void init() {
        ClientRegistrationRequest clientRegistrationRequest = new ClientRegistrationRequest(
                "client-1",
                List.of(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()),
                List.of("https://client.example.org/callback",
                        "https://client.example.org/callback2"),
                "https://client.example.org/logo",
                List.of("contact-1", "contact-2"),
                "openid email profile"
        );
        register(clientRegistrationRequest);
    }

    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oauth-client-demo")
                .clientSecret("oauth-client-demo-secret")
                .clientName("oauth client demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/{registrationId}")
                .scopes(scopes->scopes.addAll(List.of()))
                .build();

        repository.save(client);
        ClientRegistrationResponse response = new ClientRegistrationResponse();
        return response;
    }

    private ClientRegistrationResponse toDto(RegisteredClient registeredClient) {
        return null;
    }
}
