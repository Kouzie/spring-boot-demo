package com.example.auth.server.demo.client.service;

import com.example.auth.server.demo.client.JpaRegisteredClientRepository;
import com.example.auth.server.demo.client.dto.ClientRegistrationRequest;
import com.example.auth.server.demo.client.dto.ClientRegistrationResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService {
    private final JpaRegisteredClientRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final Environment env;

    @PostConstruct
    void init() {
        // Ahotirzation Server 의 Resource Client 등록을 위한 코드
        RegisteredClient.Builder registration = RegisteredClient.withId("oauth-client-demo")
                .clientId("oauth-demo-client-id")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientName("spring boot oauth demo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // for public client
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // for basic secret
                .authorizationGrantTypes(types -> {
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    types.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oauth-client-redirect")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scopes(scopes -> {
                    scopes.add("email");
                    scopes.add("profile"); // gender, birthdate, nickname
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build());
        if (env.acceptsProfiles(Profiles.of("opaque"))) {
            // registered client 의 token 타입을 지정
            registration.tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE).build()); // Reference (opaque) tokens
        } else if (env.acceptsProfiles(Profiles.of("oidc"))) {
            registration.tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build()); // Reference (JWT) tokens
            registration.scope(OidcScopes.OPENID);
        }
        repository.save(registration.build());
    }

    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        RegisteredClient registeredClient = toClient(request);
        repository.save(registeredClient);
        return toDto(registeredClient);
    }

    private RegisteredClient toClient(ClientRegistrationRequest request) {
        RegisteredClient registeredClient = RegisteredClient.withId(String.valueOf(System.currentTimeMillis()))
                .clientId(request.getClientId())
                .clientSecret(passwordEncoder.encode(request.getClientSecret()))
                .clientName(request.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantTypes(types -> {
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    types.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .postLogoutRedirectUri(request.getPostLogoutRedirectUri())
                .redirectUri(request.getRedirectUri())
                .scopes(scopes -> scopes.addAll(request.getScopes()))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .build();
        return registeredClient;
    }

    private ClientRegistrationResponse toDto(RegisteredClient registeredClient) {
        return ClientRegistrationResponse.builder()
                .id(registeredClient.getId())
                .clientId(registeredClient.getClientId())
                .clientSecret(registeredClient.getClientSecret())
                .clientName(registeredClient.getClientName())
                .authorizationGrantTypes(getGrantTypes(registeredClient.getAuthorizationGrantTypes()))
                .redirectUri(registeredClient.getRedirectUris().stream().toList().get(0))
                .postLogoutRedirectUri(registeredClient.getPostLogoutRedirectUris().stream().toList().get(0))
                .scopes(registeredClient.getScopes())
                .build();
    }

    private Set<String> getGrantTypes(Set<AuthorizationGrantType> authorizationGrantTypes) {
        return authorizationGrantTypes.stream().map(AuthorizationGrantType::getValue).collect(Collectors.toSet());
    }
}
