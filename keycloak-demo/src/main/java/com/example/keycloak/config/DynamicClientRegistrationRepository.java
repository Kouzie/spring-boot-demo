package com.example.keycloak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository {

    private final String clientId;
    private final String clientSecret;
    private final String authServerUrl;

    private final Map<String, ClientRegistration> registrations = new ConcurrentHashMap<>();

    public DynamicClientRegistrationRepository(
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret,
            @Value("${keycloak.auth-server-url:http://localhost:8080}") String authServerUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authServerUrl = authServerUrl;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        // In this implementation, registrationId is treated as the realm name
        return registrations.computeIfAbsent(registrationId, this::createClientRegistration);
    }

    private ClientRegistration createClientRegistration(String realm) {
        String issuer = authServerUrl + "/realms/" + realm;

        try {
            return ClientRegistrations.fromIssuerLocation(issuer)
                    .registrationId(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope("openid", "profile", "email", "roles")
                    .clientName("Keycloak (" + realm + ")")
                    .build();
        } catch (Exception e) {
            // In a real app, handle connection errors or invalid realms more gracefully
            throw new IllegalArgumentException("Failed to resolve realm metadata for: " + realm, e);
        }
    }
}
