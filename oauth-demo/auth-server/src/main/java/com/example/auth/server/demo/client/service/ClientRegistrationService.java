package com.example.auth.server.demo.client.service;

import com.example.auth.server.demo.client.dto.ClientRegistrationRequest;
import com.example.auth.server.demo.client.dto.ClientRegistrationResponse;
import com.example.auth.server.demo.client.model.JpaRegisteredClientRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService {
    private final JpaRegisteredClientRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void init() {
        // Ahotirzation Server 의 Resource Client 등록을 위한 코드
        RegisteredClient.Builder registration = RegisteredClient.withId("oauth-client-demo")
                .clientId("oauth-demo-client-id")
                // plaintext is secret It is encoded with BCrypt from EncodedSecretTests
                // do not include secrets in the source code because bad actors can get access to your secrets
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantTypes(types -> {
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    types.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oauth-client-redirect")
                .scopes(scopes -> {
                    scopes.add(OidcScopes.OPENID);
                    scopes.add(OidcScopes.PROFILE);
                    scopes.add(OidcScopes.EMAIL);
                    scopes.add("keys.write");
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build());
        this.save(registration.build());
    }

    public void save(RegisteredClient registeredClient) {
        repository.save(registeredClient);
    }

    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        return null;
    }

    private ClientRegistrationResponse toDto(RegisteredClient registeredClient) {
        return null;
    }
}
